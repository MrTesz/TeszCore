package de.mrtesz.dbutils.utils.sqlite;

import com.zaxxer.hikari.HikariDataSource;
import de.mrtesz.dbutils.api.DBUtils;
import de.mrtesz.dbutils.utils.logger.DebugLevel;
import de.mrtesz.dbutils.utils.utilClasses.SelectionResults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Date;
import java.sql.*;
import java.util.*;

@SuppressWarnings("unused")
public class SqliteManager extends AbstractSqliteManager {

    private final String projectName;

    public SqliteManager(boolean infoWhenCredentialsAreNull, @Nullable String path,
                         @NotNull String name, HikariDataSource dataSource, String projectName) {
        super(infoWhenCredentialsAreNull, path, name, dataSource, projectName);
        this.projectName = projectName;
    }
    public SqliteManager(boolean infoWhenCredentialsAreNull, @Nullable String path,
                         @NotNull String name, String projectName) {
        super(infoWhenCredentialsAreNull, path, name, projectName);
        this.projectName = projectName;
    }

    /**
     * Create a Sqlite Table using a Table object
     * @param sqliteTable The Table that has to be created or altered
     */
    public void createOrAlter(@NotNull SqliteTable sqliteTable) {
        String tableName = sqliteTable.getName();
        long start = System.currentTimeMillis();

        checkConnection();
        try (Connection conn = getConnection(); Statement statement = conn.createStatement()) {
            String createCmd = sqliteTable.getCreateCommand();
            statement.executeUpdate(createCmd);
            DBUtils.logging(DebugLevel.LEVEL8, projectName).debug("Created table " + tableName + " if not exists in " + (System.currentTimeMillis() - start) + " ms");

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
                        DBUtils.logging(DebugLevel.LEVEL8, projectName)
                                .debug("Added column '" + column + "' to table '" + tableName + "' in " + (System.currentTimeMillis() - start) + " ms");
                    } catch (SQLException e) {
                        DBUtils.logging(DebugLevel.LEVEL1, projectName)
                                .error("Error while adding column '" + column + "' in table '" + tableName + "': " + e.getMessage());
                        DBUtils.logging(DebugLevel.LEVEL0, projectName).logException(e);
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
                        DBUtils.logging(DebugLevel.LEVEL8, projectName)
                                .debug("Created index '" + indexName + "' on table '" + tableName + "' in " + (System.currentTimeMillis() - start) + " ms");
                    } catch (SQLException e) {
                        DBUtils.logging(DebugLevel.LEVEL1, projectName)
                                .error("Error while creating index '" + indexName + "' on table '" + tableName + "': " + e.getMessage());
                        DBUtils.logging(DebugLevel.LEVEL0, projectName).logException(e);
                    }
                }
            }
        } catch (SQLException e) {
            DBUtils.logging(DebugLevel.LEVEL1, projectName).error("Error while create/alter table '" + tableName + "' with '" + sqliteTable.getCreateCommand() + "': " + e.getMessage());
            DBUtils.logging(DebugLevel.LEVEL0, projectName).logException(e);
        }
    }

    /**
     * Execute a SQL Query <br>
     * e.g.: <code>executeSql("UPDATE playerInfos SET name = ? WHERE uuid = ?", List.of(player.getName(), player.getUniqueId()), "playerInfos", "update player name")</code>
     * @param sql The query that should be executed
     * @param values The values replacing the ?'s in the param sql
     * @param tableName The Name of the Table that is worked with
     * @param type The Type of the execution
     * @return the return value of PreparedStatement.executeUpdate
     */
    public int executeSql(String sql, @NotNull List<Object> values, String tableName, String type) {
        long start = System.currentTimeMillis();

        checkConnection();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            int i = 1;
            for (Object o : values) {
                switch (o) {
                    case null -> ps.setObject(i++, null);
                    case String s -> ps.setString(i++, s);
                    case Integer n -> ps.setInt(i++, n);
                    case Long l -> ps.setLong(i++, l);
                    case Boolean b -> ps.setBoolean(i++, b);
                    case Date d -> ps.setDate(i++, d);
                    default -> ps.setObject(i++, o);
                }
            }
            int result = ps.executeUpdate();
            DBUtils.logging(DebugLevel.LEVEL10, projectName).debug("Executed " + type + " in " + tableName + " Using: " + buildSqlWithParams(sql, values, true) + " in "
                    + (System.currentTimeMillis() - start) + " ms Result: " + result);
            return result;
        } catch (SQLException e) {
            DBUtils.logging(DebugLevel.LEVEL1, projectName).error("Error while executing " + type + " '" + buildSqlWithParams(sql, values, false) + "' in '" + tableName +
                    "' Error: " + e.getMessage());
            DBUtils.logging(DebugLevel.LEVEL0, projectName).logException(e);
        }

        return 0;
    }
    /**
     * Execute a SQL Query <br>
     * e.g.: <code>executeSql("UPDATE playerInfos SET name = " + player.getName() + " WHERE uuid = " + player.getUniqueId(), "playerInfos", "update player name")</code>
     * @param sql The query that should be executed
     * @param tableName The Name of the Table that is worked with
     * @param type The Type of the execution
     * @return the return value of PreparedStatement.executeUpdate
     */
    public int executeSql(String sql, String tableName, String type) {
        long start = System.currentTimeMillis();

        checkConnection();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            int result = ps.executeUpdate();
            DBUtils.logging(DebugLevel.LEVEL10, projectName).debug("Executed " + type + " in " + tableName + " Using: " + buildSqlWithParams(sql, List.of(), true) + " in "
                    + (System.currentTimeMillis() - start) + " ms Result: " + result);
            
            return result;
        } catch (SQLException e) {
            DBUtils.logging(DebugLevel.LEVEL1, projectName).error("Error while executing " + type + " '" + buildSqlWithParams(sql, List.of(), false) + "' in '" + tableName +
                    "' Error: " + e.getMessage());
            DBUtils.logging(DebugLevel.LEVEL0, projectName).logException(e);
        }

        return 0;
    }

    /**
     * Execute a Selection Query
     * e.g. <code>executeSelect("SELECT email, number FROM users WHERE username = ?", List.of("Mr_Tesz"), "users")</code>
     * @param sql Sql Query
     * @param questionMarks List of ?'s in the sql
     * @param tableName Name of selected Table for logging
     * @return SelectionResult wich represents a list of rows in Map<column, value>
     */
    public SelectionResults executeSelect(String sql, @NotNull List<Object> questionMarks, String tableName) {
        List<Map<String, Object>> returnValue = new ArrayList<>();
        long start = System.currentTimeMillis();

        checkConnection();
        try (Connection conn = getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
            for (int i = 0; i < questionMarks.size(); i++) {
                Object o = questionMarks.get(i);

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
                DBUtils.logging(DebugLevel.LEVEL10, projectName).debug("Selected from '" + tableName +
                        "' Using: '" + buildSqlWithParams(sql, questionMarks, true) + "' in " + (System.currentTimeMillis() - start) + " ms");
                if (!returnValue.isEmpty()) {
                    DBUtils.logging(DebugLevel.LEVEL11, projectName).debug("Results:");
                    for (Map<String, Object> map : returnValue) {
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            DBUtils.logging(DebugLevel.LEVEL11, projectName).
                                    debug("Column: " + entry.getKey()
                                            + " Value-Type: " + (entry.getValue() != null ? entry.getValue().getClass().getName() : "null (Any errors?)")
                                            + " Value: " + (entry.getValue() != null ? entry.getValue() : "null (Any errors?)"));
                        }
                    }
                } else
                    DBUtils.logging(DebugLevel.LEVEL11, projectName).debug("Results: [empty]");
            }

        } catch (SQLException e) {
            DBUtils.logging(DebugLevel.LEVEL1, projectName).error("Error while select from '" + tableName +
                    "' Command: '" + buildSqlWithParams(sql, questionMarks, false) + "' Error: " + e.getMessage());
            DBUtils.logging(DebugLevel.LEVEL0, projectName).logException(e);
        }

        return new SelectionResults(returnValue);
    }
    /**
     * Execute a Selection Query
     * e.g. <code>executeSelect("SELECT email, number FROM users WHERE username = Mr_Tesz", "users")</code>
     * @param sql Sql Query
     * @param tableName Name of selected Table for logging
     * @return SelectionResult wich represents a list of rows in Map<column, value>
     */
    public SelectionResults executeSelect(String sql, String tableName) {
        List<Map<String, Object>> returnValue = new ArrayList<>();
        long start = System.currentTimeMillis();

        checkConnection();
        try (Connection conn = getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
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
                DBUtils.logging(DebugLevel.LEVEL10, projectName).debug("Selected from '" + tableName +
                        "' Using: '" + buildSqlWithParams(sql, List.of(), true) + "' in " + (System.currentTimeMillis() - start) + " ms");
                if (!returnValue.isEmpty()) {
                    DBUtils.logging(DebugLevel.LEVEL11, projectName).debug("Results:");
                    for (Map<String, Object> map : returnValue) {
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            DBUtils.logging(DebugLevel.LEVEL11, projectName).
                                    debug("Column: " + entry.getKey()
                                            + " Value-Type: " + (entry.getValue() != null ? entry.getValue().getClass().getName() : "null (Any errors?)")
                                            + " Value: " + (entry.getValue() != null ? entry.getValue() : "null (Any errors?)"));
                        }
                    }
                } else
                    DBUtils.logging(DebugLevel.LEVEL11, projectName).debug("Results: [empty]");
            }

        } catch (SQLException e) {
            DBUtils.logging(DebugLevel.LEVEL1, projectName).error("Error while select from '" + tableName +
                    "' Command: '" + buildSqlWithParams(sql, List.of(), false) + "' Error: " + e.getMessage());
            DBUtils.logging(DebugLevel.LEVEL0, projectName).logException(e);
        }

        return new SelectionResults(returnValue);
    }

    /**
     * Insert something into a Table<br>
     * e.g.: <code>Map[String, Object] values = new HashMap<>()<br>values.put("uuid", player.getUniqueId())<br>
     * values.put("name", player.getName())<br>mariaDBManager.insertInto("playerInfos", values)</code>
     * @param tableName Name of the table, wich should be inserted in
     * @param values The values, that should be inserted
     */
    public int insertInto(String tableName, @NotNull Map<String, Object> values) {
        String columns = String.join(", ", values.keySet());
        String placeholders = String.join(", ", Collections.nCopies(values.size(), "?"));
        String sql = "INSERT INTO `" + tableName + "` (" + columns + ") VALUES (" + placeholders + ")";

        return this.executeSql(sql, values.values().stream().toList(), tableName, "insert");
    }

    public int insertIgnore(String tableName, @NotNull Map<String, Object> values) {
        String columns = String.join(", ", values.keySet());
        String placeholders = String.join(", ", Collections.nCopies(values.size(), "?"));
        String sql = "INSERT OR IGNORE INTO `" + tableName + "` (" + columns + ") VALUES (" + placeholders + ")";

        return this.executeSql(sql, values.values().stream().toList(), tableName, "insert_ignore");
    }

    /**
     * Delete an entry
     * @param tableName Name of the table, wich should be deleted from
     * @param whereClause Clause, narrowing the targeted columns
     * @param params The parameter for the ?'s in the whereClause
     */
    public int deleteFrom(String tableName, String whereClause, List<Object> params) {
        String sql = "DELETE FROM `" + tableName + "` WHERE " + whereClause;

        return this.executeSql(sql, params, tableName, "delete");
    }
    /**
     * Delete an entry
     * @param tableName Name of the table, wich should be deleted from
     * @param whereClause Clause, narrowing the targeted columns
     */
    public int deleteFrom(String tableName, String whereClause) {
        String sql = "DELETE FROM `" + tableName + "` WHERE " + whereClause;

        return this.executeSql(sql, tableName, "delete");
    }

    /**
     * Update a Table <br>
     * e.g.: <code>
     * Map[String, Object] values = new HashMap<>()<br>
     * values.put("name", player.getName())<br>
     * update("playerInfos", values, "uuid = ?", List.of(player.getUniqueId())
     * </code>
     * @param tableName Name of the table, wich should be updated
     * @param values Values that should be updated
     * @param whereClause Clause, narrowing the targeted columns
     * @param whereParams The parameter for the ?'s in the whereClause
     */
    public int update(String tableName, @NotNull Map<String, Object> values, @Nullable String whereClause, List<Object> whereParams) {
        String setClause = String.join(", ", values.keySet().stream().map(k -> "`" + k + "` = ?").toList());
        String sql = "UPDATE `" + tableName + "` SET " + setClause +
                (whereClause == null ? "" :  " WHERE " + whereClause.replace("WHERE", "").replace("  ", " "));

        List<Object> sqlValues = new ArrayList<>();
        sqlValues.addAll(values.values());
        sqlValues.addAll(whereParams);

        return this.executeSql(sql, sqlValues, tableName, "update");
    }
    /**
     * Update a Table <br>
     * e.g.: <code>
     * Map[String, Object] values = new HashMap<>()<br>
     * values.put("name", player.getName())<br>
     * update("playerInfos", values, "uuid = " + player.getUniqueId())
     * </code>
     * @param tableName Name of the table, wich should be updated
     * @param values Values that should be updated
     * @param whereClause Clause, narrowing the targeted columns
     */
    public int update(String tableName, @NotNull Map<String, Object> values, @Nullable String whereClause) {
        String setClause = String.join(", ", values.keySet().stream().map(k -> "`" + k + "` = ?").toList());
        String sql = "UPDATE `" + tableName + "` SET " + setClause +
                (whereClause == null ? "" :  " WHERE " + whereClause.replace("WHERE", "").replace("  ", " "));

        List<Object> sqlValues = new ArrayList<>(values.values());

        return this.executeSql(sql, sqlValues, tableName, "update");
    }

    public int dropTable(String tableName) {
        return this.executeSql("DROP TABLE IF EXISTS " + tableName, List.of(), tableName, "drop table");
    }

    public boolean columnExists(String tableName, String columnName) {
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

            DBUtils.logging(DebugLevel.LEVEL11, projectName).debug("Column '" + columnName + "' exists in '" + tableName + "'? -> " + returnValue + " - "
                    + (System.currentTimeMillis() - start) + " ms");

            return returnValue;
        } catch (SQLException e) {
            DBUtils.logging(DebugLevel.LEVEL1, projectName).error("Error while check if column '" + columnName + "' exists in '" + tableName + "' Error: " + e.getMessage());
            DBUtils.logging(DebugLevel.LEVEL0, projectName).logException(e);
        }
        return false;
    }

    public boolean indexExists(String tableName, String indexName) {
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

            DBUtils.logging(DebugLevel.LEVEL11, projectName).debug("Index '" + indexName + "' exists in '" + tableName + "'? -> " + returnValue + " - " +
                    (System.currentTimeMillis() - start) + " ms");
            return returnValue;
        } catch (SQLException e) {
            DBUtils.logging(DebugLevel.LEVEL1, projectName).error("Error while check if index " + indexName + " exists in " + tableName + " Error: " + e.getMessage());
            DBUtils.logging(DebugLevel.LEVEL0, projectName).logException(e);
        }
        return false;
    }
}
