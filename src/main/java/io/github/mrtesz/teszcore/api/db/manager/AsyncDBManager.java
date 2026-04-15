package io.github.mrtesz.teszcore.api.db.manager;

import io.github.mrtesz.teszcore.api.db.table.DBTable;
import io.github.mrtesz.teszcore.db.selection.SelectionResults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public interface AsyncDBManager extends DBManager {

    CompletableFuture<Void> createOrAlter(@NotNull DBTable dbTable) throws IllegalArgumentException;

    /**
     * Execute a SQL query with a {@link List} replacing the question marks in the {@code sql} <br>
     * e.g.: <br>
     * <code>
     *     executeSql("UPDATE users SET name = ? WHERE id = ?", {@link List}.of(username, userId), "users", "update username");
     * </code>
     * @param sql Query that should be executed
     * @param tableName Name of the table, querying to
     * @param type Type of the execution
     * @param sqlParams Values replacing the ?'s in the {@code sql} query
     * @return A {@link CompletableFuture} supplying the return value of the {@link PreparedStatement#executeUpdate()}
     */
    CompletableFuture<Integer> executeSql(@NotNull String sql, @NotNull String tableName, @Nullable String type, @NotNull List<Object> sqlParams);

    /**
     * Execute a SQL query <br>
     * e.g.: <br>
     * <code>
     *     executeSql("UPDATE users SET name = " + username + " WHERE id = " + userId, "users", "update username");
     * </code>
     * @param sql Query that should be executed
     * @param tableName Name of the table, querying to
     * @param type Type of the execution
     * @return A {@link CompletableFuture} supplying the return value of the {@link PreparedStatement#executeUpdate()}
     */
    CompletableFuture<Integer> executeSql(@NotNull String sql, @NotNull String tableName, @Nullable String type);

    /**
     * Execute a selection query with a {@link List} replacing the question marks in the {@code sql}<br>
     * e.g. <br>
     * <code>
     *     executeSelect("SELECT email, number FROM users WHERE username = ?", {@link List}.of("Mr_Tesz"), "users");
     * </code>
     * @param sql Sql query
     * @param tableName Name of the table, to select from
     * @param sqlParams Values replacing the ?'s in the {@code sql} query
     * @return A {@link CompletableFuture} supplying a {@link SelectionResults} object
     */
    CompletableFuture<SelectionResults> executeSelect(@NotNull String sql, @NotNull String tableName, @NotNull List<Object> sqlParams);

    /**
     * Execute a selection query <br>
     * e.g. <br>
     * <code>
     *     executeSelect("SELECT email, number FROM users WHERE username = Mr_Tesz", "users");
     * </code>
     * @param sql Sql query
     * @param tableName Name of the table, to select from
     * @return A {@link CompletableFuture} supplying a {@link SelectionResults} object
     */
    CompletableFuture<SelectionResults> executeSelect(@NotNull String sql, @NotNull String tableName);

    /**
     * Insert into a table <br>
     * e.g.: <br>
     * <code>
     *     {@link Map}{@code <String, Object>} values = new {@link HashMap}<>();<br>
     *     values.put("id", userId);<br>
     *     values.put("name", username)<br>dbManager.insertInto("users", values);
     * </code>
     * @param tableName Name of the table, inserting into
     * @param values Values to insert
     * @return A {@link CompletableFuture} supplying the return value of the {@link PreparedStatement#executeUpdate()}
     */
    CompletableFuture<Integer> insertInto(@NotNull String tableName, @NotNull Map<String, Object> values);

    /**
     * Insert into a table ignoring when a unique is duplicated <br>
     * e.g.: <br>
     * <code>
     *     {@link Map}{@code <String, Object>} values = new {@link HashMap}<>();<br>
     *     values.put("id", userId);<br>
     *     values.put("name", username);<br>
     *     dbManager.insertInto("users", values);
     * </code>
     * @param tableName Name of the table, inserting into
     * @param values Values to insert
     * @return A {@link CompletableFuture} supplying the return value of the {@link PreparedStatement#executeUpdate()}
     */
    CompletableFuture<Integer> insertIgnore(@NotNull String tableName, @NotNull Map<String, Object> values);

    /**
     * Delete entries with a {@link List} replacing the question marks in the {@code whereClause} <br>
     * e.g.: <br>
     * <code>
     *     deleteFrom("users", "userId = ?", {@link List}.of(userId));
     * </code>
     * @param tableName Name of the table, deleting from
     * @param whereClause Clause, narrowing the targeted columns
     * @param whereParams Values replacing the ?'s in the {@code whereClause}
     * @return A {@link CompletableFuture} supplying the rows affected
     */
    CompletableFuture<Integer> deleteFrom(@NotNull String tableName, @Nullable String whereClause, @NotNull List<Object> whereParams);

    /**
     * Delete entries <br>
     * e.g.: <br>
     * <code>
     *      deleteFrom("users", "userId = " + userId);
     * </code>
     * @param tableName Name of the table, deleting from
     * @param whereClause Clause, narrowing the targeted columns
     * @return A {@link CompletableFuture} supplying the rows affected
     */
    CompletableFuture<Integer> deleteFrom(@NotNull String tableName, @Nullable String whereClause);

    /**
     * Update a table with a {@link List} replacing the question marks in the {@code whereClause} <br>
     * e.g.: <br>
     * <code>
     *      {@link Map}{@code <String, Object>} values = new {@link HashMap}<>(); <br>
     *      values.put("name", username); <br>
     *      update("users", values, "id = ?", {@link List}.of(userId);
     * </code>
     * @param tableName Name of the table, updating
     * @param values Values to update
     * @param whereClause Clause, narrowing the targeted columns
     * @param whereParams Values replacing the ?'s in the {@code whereClause}
     * @return A {@link CompletableFuture} supplying the rows affected
     */
    CompletableFuture<Integer> update(@NotNull String tableName, @NotNull Map<String, Object> values, @Nullable String whereClause, @NotNull List<Object> whereParams);

    /**
     * Update a table <br>
     * e.g.: <br>
     * <code>
     *      {@link Map}{@code <String, Object>} values = new {@link HashMap}<>(); <br>
     *      values.put("name", username); <br>
     *      update("users", values, "id = " + userId);
     * </code>
     * @param tableName Name of the table, updating
     * @param values Values to update
     * @param whereClause Clause, narrowing the targeted columns
     * @return A {@link CompletableFuture} supplying the rows affected
     */
    CompletableFuture<Integer> update(@NotNull String tableName, @NotNull Map<String, Object> values, @Nullable String whereClause);

    /**
     * Drop a table from the database <br>
     * e.g.: <br>
     * <code>
     *     dropTable("current-weak-save");
     * </code>
     * @param tableName Name of the table to drop
     * @return A {@link CompletableFuture} supplying the return value of the {@link PreparedStatement#executeUpdate()}
     */
    CompletableFuture<Integer> dropTable(@NotNull String tableName);

    /**
     * Show if a column exists in a table
     * @param tableName Name of the table, checking
     * @param columnName Name of the column to be checked
     * @return A {@link CompletableFuture} supplying true if the column exists
     */
    CompletableFuture<Boolean> columnExists(@NotNull String tableName, @NotNull String columnName);

    /**
     * Show if an index exists in a table
     * @param tableName Name of the table, checking
     * @param indexName Name of the index to be checked
     * @return A {@link CompletableFuture} supplying true if the index exists
     */
    CompletableFuture<Boolean> indexExists(@NotNull String tableName, @NotNull String indexName);
}
