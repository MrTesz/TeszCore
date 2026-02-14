package de.mrtesz.dbutils.utils.db.manager.mariadb;

import com.zaxxer.hikari.HikariDataSource;
import de.mrtesz.dbutils.api.DBUtils;
import de.mrtesz.dbutils.api.db.manager.AsyncDBManager;
import de.mrtesz.dbutils.api.db.table.DBTable;
import de.mrtesz.dbutils.utils.logger.api.DebugLevel;
import de.mrtesz.dbutils.utils.db.selection.SelectionResults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Date;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class AsyncMariaDBManager extends AbstractMariaDBManager implements AsyncDBManager {

    private final String projectName;
    protected final int timeoutSeconds;

    protected AsyncMariaDBManager(boolean infoWhenCredentialsAreNull, @Nullable String name, @Nullable String url, @Nullable String user, @Nullable String password,
                                  @Nullable HikariDataSource dataSource, @Nullable String projectName, int timeoutSeconds) {
        super(infoWhenCredentialsAreNull, (name == null ? "Main" : name), url, user, password, dataSource, projectName);
        this.timeoutSeconds = timeoutSeconds;
        this.projectName = projectName;
    }

    /**
     * Create a MariaDB Table using a {@link MariaDBTable} object
     * @param dbTable The Table that is created or altered
     * @throws IllegalArgumentException when the {@code dbTable} param is not a {@link MariaDBTable}
     */
    @Override
    public CompletableFuture<Void> createOrAlter(@NotNull DBTable dbTable) throws IllegalArgumentException {
        if (!(dbTable instanceof MariaDBTable mariaDBTable)) throw new IllegalArgumentException("DBTable object was not " + MariaDBTable.class.getName() + " but " + dbTable.getClass().getName() + ".");

        String name = mariaDBTable.getName();
        long start = System.currentTimeMillis();

        checkConnection();
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = getConnection(); Statement statement = conn.createStatement()) {
                String createCmd = mariaDBTable.getCreateCommand();
                statement.executeUpdate(createCmd);
                DBUtils.logging(DebugLevel.LEVEL8, projectName).debug("Created table " + name + " if not exists in " + (System.currentTimeMillis() - start) + " ms");

                for (Map.Entry<String, String> entry : mariaDBTable.getAlterColumnsCommands().entrySet()) {
                    columnExists(name, entry.getKey()).thenAcceptAsync(b -> {
                        if (!b) {
                            try {
                                statement.executeUpdate(entry.getValue());
                                DBUtils.logging(DebugLevel.LEVEL8, projectName).debug("Altered Table " + name + "'s column in " + (System.currentTimeMillis() - start) + " ms");
                            } catch (SQLException e) {
                                DBUtils.logging(DebugLevel.LEVEL1, projectName).error("Error while altering table '" + name + "' with '" + entry.getValue() + "': " + e.getMessage());
                                DBUtils.logging(DebugLevel.LEVEL0, projectName).logException(e);
                            }
                        }
                    });
                }
                for (Map.Entry<String, String> entry : mariaDBTable.getAlterIndexCommands().entrySet()) {
                    indexExists(name, entry.getKey()).thenAcceptAsync(b -> {
                        if (!b) {
                            try {
                                statement.executeUpdate(entry.getValue());
                                DBUtils.logging(DebugLevel.LEVEL8, projectName).debug("Altered Table " + name + "'s index in " + (System.currentTimeMillis() - start) + " ms");
                            } catch (SQLException e) {

                                DBUtils.logging(DebugLevel.LEVEL1, projectName).error("Error while altering table '" + name + "' with '" + entry.getValue() + "': " + e.getMessage());
                                DBUtils.logging(DebugLevel.LEVEL0, projectName).logException(e);
                            }
                        }
                    });
                }
            } catch (SQLException e) {
                DBUtils.logging(DebugLevel.LEVEL1, projectName).error("Error while create/alter table '" + name + "' with '" + mariaDBTable.getCreateCommand() + "': " + e.getMessage());
                DBUtils.logging(DebugLevel.LEVEL0, projectName).logException(e);
            }
        })
                .completeOnTimeout(null, timeoutSeconds, TimeUnit.SECONDS)
                .whenComplete(
                        (result, ex) -> {
                            if (ex == null)
                                DBUtils.logging(DebugLevel.LEVEL0, projectName).warning("Async executeSql task of AsyncMariaDBManager " + getName() + " of Project " + projectName
                                        + " timed out after " + timeoutSeconds + " seconds.");
                            if (ex != null)
                                DBUtils.logging(DebugLevel.LEVEL0, projectName).logException(ex);
                        }
                );
    }

    @Override
    public CompletableFuture<Integer> executeSql(@NotNull String sql, @NotNull List<Object> sqlParams, @NotNull String tableName, @Nullable String type) {
        long start = System.currentTimeMillis();

        checkConnection();
        return CompletableFuture.supplyAsync(() -> {
                    try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                        int i = 1;
                        for (Object o : sqlParams) {
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
                        DBUtils.logging(DebugLevel.LEVEL10, projectName).debug("Executed " + (type == null ? "unspecified" : type) + " in " + tableName + " Using: " + buildSqlWithParams(sql, sqlParams, true) + " in "
                                + (System.currentTimeMillis() - start) + " ms Result: " + result);
                        return result;
                    } catch (SQLException e) {
                        DBUtils.logging(DebugLevel.LEVEL1, projectName).error("Error while executing " + (type == null ? "unspecified" : type) + " '" + buildSqlWithParams(sql, sqlParams, false) + "' in '" + tableName +
                                "' Error: " + e.getMessage());
                        DBUtils.logging(DebugLevel.LEVEL0, projectName).logException(e);
                    }

                    return 0;
                })
                .completeOnTimeout(null, timeoutSeconds, TimeUnit.SECONDS)
                .whenComplete(
                        (result, ex) -> {
                            if (result == null && ex == null)
                                DBUtils.logging(DebugLevel.LEVEL0, projectName).warning("Async executeSql task of AsyncMariaDBManager " + getName() + " of Project " + projectName
                                        + " timed out after " + timeoutSeconds + " seconds.");
                            if (ex != null)
                                DBUtils.logging(DebugLevel.LEVEL0, projectName).logException(ex);
                        }
                );
    }

    @Override
    public CompletableFuture<Integer> executeSql(@NotNull String sql, @NotNull String tableName, @Nullable String type) {
        return executeSql(sql, List.of(), tableName, type);
    }

    @Override
    public CompletableFuture<SelectionResults> executeSelect(@NotNull String sql, @NotNull List<Object> sqlParams, @NotNull String tableName) {
        List<Map<String, Object>> returnValue = new ArrayList<>();
        long start = System.currentTimeMillis();

        checkConnection();
        return CompletableFuture.supplyAsync(() -> {
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
                    DBUtils.logging(DebugLevel.LEVEL10, projectName).debug("Selected from '" + tableName +
                            "' Using: '" + buildSqlWithParams(sql, sqlParams, true) + "' in " + (System.currentTimeMillis() - start) + " ms");
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
                        "' Command: '" + buildSqlWithParams(sql, sqlParams, false) + "' Error: " + e.getMessage());
                DBUtils.logging(DebugLevel.LEVEL0, projectName).logException(e);
            }
            return new SelectionResults(returnValue);
        }).completeOnTimeout(null, timeoutSeconds, TimeUnit.SECONDS).whenComplete(
                (result, ex) -> {
                    if (ex == null)
                        DBUtils.logging(DebugLevel.LEVEL0, projectName).warning("Async executeSelect task of AsyncMariaDBManager " + getName() + " of Project " + projectName
                                + " timed out after " + timeoutSeconds + " seconds.");
                }
        );
    }

    @Override
    public CompletableFuture<SelectionResults> executeSelect(@NotNull String sql, @NotNull String tableName) {
        return executeSelect(sql, List.of(), tableName);
    }

    @Override
    public CompletableFuture<Integer> insertInto(@NotNull String tableName, @NotNull Map<String, Object> values) {
        String columns = String.join(", ", values.keySet());
        String placeholders = String.join(", ", Collections.nCopies(values.size(), "?"));
        String sql = "INSERT INTO `" + tableName + "` (" + columns + ") VALUES (" + placeholders + ")";

        return this.executeSql(sql, values.values().stream().toList(), tableName, "insert");
    }

    @Override
    public CompletableFuture<Integer> insertIgnore(@NotNull String tableName, @NotNull Map<String, Object> values) {
        String columns = String.join(", ", values.keySet());
        String placeholders = String.join(", ", Collections.nCopies(values.size(), "?"));
        String sql = "INSERT IGNORE INTO `" + tableName + "` (" + columns + ") VALUES (" + placeholders + ")";

        return this.executeSql(sql, values.values().stream().toList(), tableName, "insert_ignore");
    }

    @Override
    public CompletableFuture<Integer> deleteFrom(@NotNull String tableName, @Nullable String whereClause, @NotNull List<Object> whereParams) {
        String sql = "DELETE FROM `" + tableName + "`" + (whereClause != null ? " WHERE " + whereClause.replaceFirst("WHERE", "") : "");

        return this.executeSql(sql, whereParams, tableName, "delete");
    }

    @Override
    public CompletableFuture<Integer> deleteFrom(@NotNull String tableName, @Nullable String whereClause) {
        return deleteFrom(tableName, whereClause, List.of());
    }

    @Override
    public CompletableFuture<Integer> update(@NotNull String tableName, @NotNull Map<String, Object> values, @Nullable String whereClause, @NotNull List<Object> whereParams) {
        String setClause = String.join(", ", values.keySet().stream().map(k -> "`" + k + "` = ?").toList());
        String sql = "UPDATE `" + tableName + "` SET " + setClause +
                (whereClause == null ? "" :  " WHERE " + whereClause.replaceFirst("WHERE", ""));

        List<Object> sqlValues = new ArrayList<>();
        sqlValues.addAll(values.values());
        sqlValues.addAll(whereParams);

        return this.executeSql(sql, sqlValues, tableName, "update");
    }

    @Override
    public CompletableFuture<Integer> update(@NotNull String tableName, @NotNull Map<String, Object> values, @Nullable String whereClause) {
        return update(tableName, values, whereClause, List.of());
    }

    @Override
    public CompletableFuture<Integer> dropTable(@NotNull String tableName) {
        return this.executeSql("DROP TABLE IF EXISTS " + tableName, List.of(), tableName, "drop table");
    }

    @Override
    public CompletableFuture<Boolean> columnExists(@NotNull String tableName, @NotNull String columnName) {
        long start = System.currentTimeMillis();
        checkConnection();

        return CompletableFuture.supplyAsync(
                () -> {
                    try (Connection conn = getConnection(); PreparedStatement statement = conn.prepareStatement("SHOW COLUMNS FROM " + tableName + " LIKE ?")) {
                        statement.setString(1, columnName);

                        boolean returnValue;
                        try (ResultSet set = statement.executeQuery()) {
                            returnValue = set.next();
                        }
                        DBUtils.logging(DebugLevel.LEVEL11, projectName).debug("Column '" + columnName + "' exists in '" + tableName + "'? -> " + returnValue + " - "
                                + (System.currentTimeMillis() - start) + " ms");
                        return returnValue;
                    } catch (SQLException e) {
                        DBUtils.logging(DebugLevel.LEVEL1, projectName).error("Error while check if column '" + columnName + "' exists in '" + tableName + "' Error: " + e.getMessage());
                        DBUtils.logging(DebugLevel.LEVEL0, projectName).logException(e);
                        return false;
                    }
                }
        ).completeOnTimeout(false, timeoutSeconds, TimeUnit.SECONDS).whenComplete(
                (result, ex) -> {
                    if (ex == null)
                        DBUtils.logging(DebugLevel.LEVEL0, projectName).warning("Async columnExists task of AsyncMariaDBManager " + getName() + " of Project " + projectName
                                + " timed out after " + timeoutSeconds + " seconds.");
                }
        );
    }

    @Override
    public CompletableFuture<Boolean> indexExists(@NotNull String tableName, @NotNull String indexName) {
        long start = System.currentTimeMillis();
        checkConnection();

        return CompletableFuture.supplyAsync(
                () -> {
                    try (Connection conn = getConnection(); PreparedStatement statement = conn.prepareStatement("SHOW INDEX FROM " + tableName + " WHERE Key_name = ?")) {
                        statement.setString(1, indexName);

                        boolean returnValue;
                        try (ResultSet set = statement.executeQuery()) {
                            returnValue = set.next();
                        }
                        DBUtils.logging(DebugLevel.LEVEL11, projectName).debug("Index '" + indexName + "' exists in '" + tableName + "'? -> " + returnValue + " - " +
                                (System.currentTimeMillis() - start) + " ms");
                        return returnValue;
                    } catch (SQLException e) {
                        DBUtils.logging(DebugLevel.LEVEL1, projectName).error("Error while check if index " + indexName + " exists in " + tableName + " Error: " + e.getMessage());
                        DBUtils.logging(DebugLevel.LEVEL0, projectName).logException(e);
                        return false;
                    }
                }
        ).completeOnTimeout(false, timeoutSeconds, TimeUnit.SECONDS).whenComplete(
                (result, ex) -> {
                    if (ex == null)
                        DBUtils.logging(DebugLevel.LEVEL0, projectName).warning("Async indexExists task of AsyncMariaDBManager " + getName() + " of Project " + projectName
                                + " timed out after " + timeoutSeconds + " seconds.");
                }
        );
    }
}
