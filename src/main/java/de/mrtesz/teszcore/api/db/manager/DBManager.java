package de.mrtesz.teszcore.api.db.manager;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;

public interface DBManager {

    String getName();

    boolean isClosed();
    void close();

    void connect();
    void disconnect();
    void checkConnection();

    Connection getConnection();

    /**
     * Calls {@link #async(int)} with timeoutSecs being 2
     */
    default AsyncDBManager async() {
        return async(2);
    }
    AsyncDBManager async(int timeoutSeconds);

    SyncDBManager sync();

    /**
     * Build an SqlQuery with params for logging
     * @param sql Sql Command with ?'s
     * @param params Parameter for ?'s in sql
     * @param cut Shorten SELECT, WHERE, FROM to SEL, WRE, FR
     * @return SQL Command with all Parameters
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