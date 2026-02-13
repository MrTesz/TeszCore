package de.mrtesz.dbutils.api.db.manager;

import de.mrtesz.dbutils.api.db.table.DBTable;
import de.mrtesz.dbutils.utils.selection.SelectionResults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public interface SyncDBManager extends DBManager {

    void createOrAlter(@NotNull DBTable dbTable);

    /**
     * Execute a SQL query with a {@link List} replacing the question marks in the {@code sql} <br>
     * e.g.: <br>
     * <code>
     *     executeSql("UPDATE users SET name = ? WHERE id = ?", {@link List}.of(username, userId), "users", "update username");
     * </code>
     * @param sql Query that should be executed
     * @param sqlParams Values replacing the ?'s in the {@code sql} query
     * @param tableName Name of the table, querying to
     * @param type Type of execution
     * @return Return value of the {@link PreparedStatement#executeUpdate()}
     */
    int executeSql(@NotNull String sql, @NotNull List<Object> sqlParams, @NotNull String tableName, @Nullable String type);

    /**
     * Execute a SQL query <br>
     * e.g.: <br> <code>
     *     executeSql("UPDATE users SET name = " + username + " WHERE id = " + userId, "users", "update username");
     * </code>
     * @param sql Query that should be executed
     * @param tableName Name of the table, querying to
     * @param type Type of execution
     * @return Return value of {@link PreparedStatement#executeUpdate()}
     */
    int executeSql(@NotNull String sql, String tableName, String type);

    /**
     * Execute a selection query with a {@link List} replacing the question marks in the {@code sql} <br>
     * e.g. <br>
     * <code>
     *     executeSelect("SELECT email, number FROM users WHERE username = ?", {@link List}.of("Mr_Tesz"), "users");
     * </code>
     * @param sql Sql query
     * @param sqlParams Values replacing the ?'s in the {@code sql} query
     * @param tableName Name of the table, to select from
     * @return {@link SelectionResults} object representing the result
     */
    SelectionResults executeSelect(@NotNull String sql, @NotNull List<Object> sqlParams, @NotNull String tableName);

    /**
     * Execute a selection query <br>
     * e.g. <br>
     * <code>
     *     executeSelect("SELECT email, number FROM users WHERE username = Mr_Tesz", "users");
     * </code>
     * @param sql Sql query
     * @param tableName Name of the table, to select from
     * @return {@link SelectionResults} object representing the result
     */
    SelectionResults executeSelect(@NotNull String sql, @NotNull String tableName);

    /**
     * Insert into a table <br>
     * e.g.: <br>
     * <code>
     *     {@link Map}{@code <String, Object>} values = new {@link HashMap}<>(); <br>
     *     values.put("id", userId); <br>
     *     values.put("name", username); <br>
     *     dbManager.insertInto("users", values);
     * </code>
     * @param tableName Name of the table, inserting into
     * @param values Values to insert
     * @return Return value of {@link PreparedStatement#executeUpdate()}
     */
    int insertInto(@NotNull String tableName, @NotNull Map<String, Object> values);

    /**
     * Insert into a table ignoring when a unique is duplicated <br>
     * e.g.: <br>
     * <code>
     *     {@link Map}{@code <String, Object>} values = new {@link HashMap}<>(); <br>
     *     values.put("id", userId); <br>
     *     values.put("name", username); <br>
     *     dbManager.insertInto("users", values);
     * </code>
     * @param tableName Name of the table, inserting into
     * @param values Values to insert
     * @return Return value of {@link PreparedStatement#executeUpdate()}
     */
    int insertIgnore(@NotNull String tableName, @NotNull Map<String, Object> values);

    /**
     * Delete entries with a {@link List} replacing the question marks in the {@code whereClause}
     * e.g.: <br>
     * <code>
     *     deleteFrom("users", "userId = ?", {@link List}.of(userId));
     * </code>
     * @param tableName Name of the table, deleting from
     * @param whereClause Clause, narrowing the targeted columns
     * @param whereParams Values replacing the ?'s in the {@code whereClause}
     * @return Number of rows affected
     */
    int deleteFrom(@NotNull String tableName, @Nullable String whereClause, @NotNull List<Object> whereParams);

    /**
     * Delete entries
     * e.g.: <br>
     * <code>
     *      deleteFrom("users", "userId = " + userId);
     * </code>
     * @param tableName Table name
     * @param whereClause WHERE clause
     * @return Number of rows affected
     */
    int deleteFrom(@NotNull String tableName, @Nullable String whereClause);

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
     * @return Number of rows affected
     */
    int update(@NotNull String tableName, @NotNull Map<String, Object> values, @Nullable String whereClause, @NotNull List<Object> whereParams);

    /**
     * Update a table <br>
     * e.g.: <br> <code>
     * <code>
     *      {@link Map}{@code <String, Object>} values = new {@link HashMap}<>(); <br>
     *      values.put("name", username); <br>
     *      update("users", values, "id = " + userId);
     * </code>
     * @param tableName Name of the table, updating
     * @param values Values to update
     * @param whereClause Clause, narrowing the targeted columns
     * @return Number of rows affected
     */
    int update(@NotNull String tableName, @NotNull Map<String, Object> values, @Nullable String whereClause);

    /**
     * Drop a table from the database
     * e.g.: <br>
     * <code>
     *     dropTable("current-weak-save");
     * </code>
     * @param tableName Name of the table, dropping from
     * @return Return value of {@link PreparedStatement#executeUpdate()}
     */
    int dropTable(@NotNull String tableName);

    /**
     * Show if a column exists in a table
     * @param tableName Name of the table, checking
     * @param columnName Name of the column to be checked
     * @return true if the column exists
     */
    boolean columnExists(@NotNull String tableName, @NotNull String columnName);

    /**
     * Show if an index exists in a table
     * @param tableName Name of the table, checking
     * @param indexName Name of the index to be checked
     * @return true if the index exists
     */
    boolean indexExists(@NotNull String tableName, @NotNull String indexName);
}
