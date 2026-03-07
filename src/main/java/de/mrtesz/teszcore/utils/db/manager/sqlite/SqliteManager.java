package de.mrtesz.teszcore.utils.db.manager.sqlite;

import com.zaxxer.hikari.HikariDataSource;
import de.mrtesz.teszcore.api.TeszCoreApi;
import de.mrtesz.teszcore.api.db.manager.SyncDBManager;
import de.mrtesz.teszcore.api.db.table.DBTable;
import de.mrtesz.teszcore.utils.copyable.Copyable;
import de.mrtesz.teszcore.utils.db.manager.mariadb.MariaDBTable;
import de.mrtesz.teszcore.utils.db.selection.SelectionResults;
import de.mrtesz.teszcore.utils.logger.level.DebugLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Date;
import java.sql.*;
import java.util.*;

public class SqliteManager extends AbstractSqliteManager implements SyncDBManager, Copyable<SqliteManager> {

    public SqliteManager(@Nullable String path, @NotNull String name, @Nullable HikariDataSource dataSource, @Nullable String projectName) {
        super(path, name, dataSource, projectName);
    }

    /**
     * Create a Sqlite table using a {@link SqliteTable} object
     * @param dbTable The Table that is created or altered
     * @throws IllegalArgumentException when the {@code dbTable} param is not a {@link SqliteTable}
     */
    public void createOrAlter(@NotNull DBTable dbTable) throws IllegalArgumentException {
        if(!(dbTable instanceof de.mrtesz.teszcore.utils.db.manager.sqlite.SqliteTable sqliteTable)) throw new IllegalArgumentException("DBTable object was not " + MariaDBTable.class.getName() + " but " + dbTable.getClass().getName() + ".");

        @NotNull String tableName = sqliteTable.getName();
        long start = System.currentTimeMillis();

        checkConnection();
        try (Connection conn = getConnection(); Statement statement = conn.createStatement()) {
            String createCmd = sqliteTable.getCreateCommand();
            statement.executeUpdate(createCmd);

            TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL8, projectName).debug("Created table " + tableName + " if not exists in " + (System.currentTimeMillis() - start) + " ms");

            Set<String> existingColumns = new HashSet<>();
            try (ResultSet rs = statement.executeQuery("PRAGMA table_info(`" + tableName + "`)")) {
                while (rs.next()) {
                    existingColumns.add(rs.getString("name"));
                }
            }

            for (Map.Entry<String, String> entry : sqliteTable.getAlterColumnsCommands().entrySet()) {
                String column = entry.getKey();
                String sql = entry.getValue();
                if (!existingColumns.contains(column)) {
                    try {
                        statement.executeUpdate(sql);
                        TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL8, projectName)
                                .debug("Added column '" + column + "' to table '" + tableName + "' in " + (System.currentTimeMillis() - start) + " ms");
                    } catch (SQLException e) {
                        TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL1, projectName)
                                .error("Error while adding column '" + column + "' in table '" + tableName + "': " + e.getMessage());
                        TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL0, projectName).logException(e);
                    }
                }
            }

            Set<String> existingIndexes = new HashSet<>();
            try (ResultSet rs = statement.executeQuery("PRAGMA index_list(`" + tableName + "`)")) {
                while (rs.next()) {
                    existingIndexes.add(rs.getString("name"));
                }
            }

            for (Map.Entry<String, String> entry : sqliteTable.getAlterIndexCommands().entrySet()) {
                String indexName = entry.getKey();
                String sql = entry.getValue();
                if (!existingIndexes.contains(indexName)) {
                    try {
                        statement.executeUpdate(sql);
                        TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL8, projectName)
                                .debug("Created index '" + indexName + "' on table '" + tableName + "' in " + (System.currentTimeMillis() - start) + " ms");
                    } catch (SQLException e) {
                        TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL1, projectName)
                                .error("Error while creating index '" + indexName + "' on table '" + tableName + "': " + e.getMessage());
                        TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL0, projectName).logException(e);
                    }
                }
            }
        } catch (SQLException e) {
            TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL1, projectName).error("Error while create/alter table '" + tableName + "' with '" + sqliteTable.getCreateCommand() + "': " + e.getMessage());
            TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL0, projectName).logException(e);
        }
    }

    @Override
    public int executeSql(@NotNull String sql, @NotNull List<Object> sqlParams, @NotNull String tableName, @Nullable String type) {
        long start = System.currentTimeMillis();

        checkConnection();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            int i = 1;
            for (Object o : sqlParams) {
                switch (o) {
                    case null -> ps.setObject(i++, null);
                    case String s -> ps.setString(i++, s);
                    case Integer n -> ps.setInt(i++, n);
                    case Long l -> ps.setLong(i++, l);
                    case Date d -> ps.setDate(i++, d);
                    default -> ps.setObject(i++, o);
                }
            }
            int result = ps.executeUpdate();
            TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL10, projectName).debug("Executed " + (type == null ? "unspecified" : type) + " in " + tableName + " Using: " + buildSqlWithParams(sql, sqlParams, true) + " in "
                    + (System.currentTimeMillis() - start) + " ms Result: " + result);
            return result;
        } catch (SQLException e) {
            TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL1, projectName).error("Error while executing " + (type == null ? "unspecified" : type) + " '" + buildSqlWithParams(sql, sqlParams, false) + "' in '" + tableName +
                    "' Error: " + e.getMessage());
            TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL0, projectName).logException(e);
        }

        return 0;
    }

    @Override
    public int executeSql(@NotNull String sql, @NotNull String tableName, @Nullable String type) {
        return executeSql(sql, List.of(), tableName, type);
    }

    @Override
    public SelectionResults executeSelect(@NotNull String sql, @NotNull List<Object> sqlParams, @NotNull String tableName) {
        List<Map<String, Object>> returnValue = new ArrayList<>();
        long start = System.currentTimeMillis();

        checkConnection();
        try (Connection conn = getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
            for (int i = 0; i < sqlParams.size(); i++) {
                Object o = sqlParams.get(i);

                switch (o) {
                    case null -> statement.setObject(i + 1, null);
                    case String s -> statement.setString(i + 1, s);
                    case Integer n -> statement.setInt(i + 1, n);
                    case Long l -> statement.setLong(i + 1, l);
                    case Boolean b -> statement.setBoolean(i + 1, b);
                    default -> statement.setObject(i + 1, o);
                }
            }

            try (ResultSet rs = statement.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> thisRow = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnLabel(i);
                        Object value = rs.getObject(i);
                        thisRow.put(columnName, value);
                    }
                    returnValue.add(thisRow);
                }
                // kein rs.close wegen try
                TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL10, projectName).debug("Selected from '" + tableName +
                        "' Using: '" + buildSqlWithParams(sql, sqlParams, true) + "' in " + (System.currentTimeMillis() - start) + " ms");
                if (!returnValue.isEmpty()) {
                    TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL11, projectName).debug("Results:");
                    for (Map<String, Object> map : returnValue) {
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL11, projectName).
                                    debug("Column: " + entry.getKey()
                                            + " Value-Type: " + (entry.getValue() != null ? entry.getValue().getClass().getName() : "null (Any errors?)")
                                            + " Value: " + (entry.getValue() != null ? entry.getValue() : "null (Any errors?)"));
                        }
                    }
                } else
                    TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL11, projectName).debug("Results: [empty]");
            }

        } catch (SQLException e) {
            TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL1, projectName).error("Error while select from '" + tableName +
                    "' Command: '" + buildSqlWithParams(sql, sqlParams, false) + "' Error: " + e.getMessage());
            TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL0, projectName).logException(e);
        }

        return new SelectionResults(returnValue);
    }

    @Override
    public SelectionResults executeSelect(@NotNull String sql, @NotNull String tableName) {
        return executeSelect(sql, List.of(), tableName);
    }

    @Override
    public int insertInto(@NotNull String tableName, @NotNull Map<String, Object> values) {
        String columns = String.join(", ", values.keySet());
        String placeholders = String.join(", ", Collections.nCopies(values.size(), "?"));
        String sql = "INSERT INTO `" + tableName + "` (" + columns + ") VALUES (" + placeholders + ")";

        return this.executeSql(sql, values.values().stream().toList(), tableName, "insert");
    }

    @Override
    public int insertIgnore(@NotNull String tableName, @NotNull Map<String, Object> values) {
        String columns = String.join(", ", values.keySet());
        String placeholders = String.join(", ", Collections.nCopies(values.size(), "?"));
        String sql = "INSERT OR IGNORE INTO `" + tableName + "` (" + columns + ") VALUES (" + placeholders + ")";

        return this.executeSql(sql, values.values().stream().toList(), tableName, "insert_ignore");
    }

    @Override
    public int deleteFrom(@NotNull String tableName, @Nullable String whereClause, @NotNull List<Object> whereParams) {
        String sql = "DELETE FROM `" + tableName + "`" + (whereClause != null ? "WHERE " + whereClause.replaceFirst("WHERE", "") : "");

        return this.executeSql(sql, whereParams, tableName, "delete");
    }

    @Override
    public int deleteFrom(@NotNull String tableName, String whereClause) {
        return deleteFrom(tableName, whereClause, List.of());
    }

    @Override
    public int update(@NotNull String tableName, @NotNull Map<String, Object> values, @Nullable String whereClause, @NotNull List<Object> whereParams) {
        String setClause = String.join(", ", values.keySet().stream().map(k -> "`" + k + "` = ?").toList());
        String sql = "UPDATE `" + tableName + "` SET " + setClause +
                (whereClause == null ? "" :  " WHERE " + whereClause.replaceFirst("WHERE", ""));

        List<Object> sqlValues = new ArrayList<>();
        sqlValues.addAll(values.values());
        sqlValues.addAll(whereParams);

        return this.executeSql(sql, sqlValues, tableName, "update");
    }

    @Override
    public int update(@NotNull String tableName, @NotNull Map<String, Object> values, @Nullable String whereClause) {
        return update(tableName, values, whereClause, List.of());
    }

    @Override
    public int dropTable(@NotNull String tableName) {
        return this.executeSql("DROP TABLE IF EXISTS " + tableName, List.of(), tableName, "drop table");
    }

    @Override
    public boolean columnExists(@NotNull String tableName, @NotNull String columnName) {
        long start = System.currentTimeMillis();

        checkConnection();
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement("PRAGMA table_info(`" + tableName + "`)");
             ResultSet rs = statement.executeQuery()) {

            boolean returnValue = false;
            while (rs.next()) {
                if (columnName.equals(rs.getObject("name"))) {
                    returnValue = true;
                    break;
                }
            }

            TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL11, projectName).debug("Column '" + columnName + "' exists in '" + tableName + "'? -> " + returnValue + " - "
                    + (System.currentTimeMillis() - start) + " ms");

            return returnValue;
        } catch (SQLException e) {
            TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL1, projectName).error("Error while check if column '" + columnName + "' exists in '" + tableName + "' Error: " + e.getMessage());
            TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL0, projectName).logException(e);
        }
        return false;
    }

    @Override
    public boolean indexExists(@NotNull String tableName, @NotNull String indexName) {
        long start = System.currentTimeMillis();

        checkConnection();
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement("PRAGMA index_list(`" + tableName + "`)");
             ResultSet rs = statement.executeQuery()) {

            boolean returnValue = false;
            while (rs.next()) {
                if (indexName.equalsIgnoreCase(rs.getString("name"))) {
                    returnValue = true;
                    break;
                }
            }

            TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL11, projectName).debug("Index '" + indexName + "' exists in '" + tableName + "'? -> " + returnValue + " - " +
                    (System.currentTimeMillis() - start) + " ms");
            return returnValue;
        } catch (SQLException e) {
            TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL1, projectName).error("Error while check if index " + indexName + " exists in " + tableName + " Error: " + e.getMessage());
            TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL0, projectName).logException(e);
        }
        return false;
    }

    @Override
    public SqliteManager copy() {
        return new SqliteManager(this.path, this.name, this.dataSource, this.projectName);
    }
}
