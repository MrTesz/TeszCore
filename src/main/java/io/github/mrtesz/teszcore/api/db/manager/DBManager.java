package io.github.mrtesz.teszcore.api.db.manager;

import io.github.mrtesz.teszcore.copyable.Copyable;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/** Parent interface of all database managers **/
public interface DBManager extends Copyable<DBManager> {

    /**
     * Get the name of this database manager
     * @return name
     */
    String getName();

    /**
     * Show if the database manager is closed
     * @return true if the database manager is closed
     */
    boolean isClosed();
    /**
     * Close the database manager
     */
    void close();

    /**
     * Connect the database manager with the database
     * @throws IllegalStateException if the DBManager is closed
     */
    void connect() throws IllegalStateException;
    /** Close the database manager **/
    void disconnect();
    /**
     * Connects the manager to the database again, when it is not connected
     * @throws IllegalStateException when the DBManager is closed
     */
    void checkConnection() throws IllegalStateException;

    /**
     * calls {@link #checkConnection()} and returns a database connection
     * @return a database connection
     * @throws IllegalStateException when the DBManager is closed
     */
    Connection getConnection() throws IllegalStateException;

    /**
     * Calls {@link #async(int)} with timeoutSecs being 2
     * @return an AsyncDBManager with the values initialized with
     */
    default AsyncDBManager async() {
        return async(2);
    }
    /**
     * Creates an AsyncDBManager with same information as the current database manager
     * @param timeoutSeconds The {@link CompletableFuture#completeOnTimeout} time in seconds
     * @return an asynchronous copy of the current database manager
     */
    AsyncDBManager async(int timeoutSeconds);

    /**
     * Creates a SyncDBManager with same information as the current database manager (When called from an AsyncDBManager the param timeoutSeconds will be forgotten)
     * @return a synchronous copy of the current database manager
     */
    SyncDBManager sync();

    /**
     * Creates a SQL statement by replacing question marks with the params list
     * @param sql SQL Command with ?'s
     * @param params Parameter for ?'s in SQL
     * @param cut Shorten SELECT, WHERE, FROM to SEL, WRE, FR
     * @return the SQL with replaced question marks
     */
    default String buildSqlWithParams(String sql, @NotNull List<Object> params, boolean cut) {
        for (Object param : params) {
            String value;
            if (param == null) {
                value = "NULL";
            } else if (param instanceof String || param instanceof UUID) {
                value = "'" + param.toString().replace("'", "\"") + "'";
            } else
                value = param.toString();
            sql = sql.replaceFirst("\\?", value);
        }
        if(!cut)
            return sql;

        return sql
                .replace("SELECT", "SEL")
                .replace("FROM", "FR")
                .replace("WHERE", "WRE");
    }
}