package de.mrtesz.dbutils.utils.sqlite;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.mrtesz.dbutils.api.DBUtils;
import de.mrtesz.dbutils.utils.abstracts.AbstractDBManager;
import de.mrtesz.dbutils.utils.logger.DebugLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public abstract class AbstractSqliteManager extends AbstractDBManager {

    @Getter private HikariDataSource dataSource;
    private final boolean infoWhenCredentialsAreNull;
    @Getter private final String name;
    private final String url;
    private final String projectName;

    protected AbstractSqliteManager(boolean infoWhenCredentialsAreNull, @Nullable String path, @NotNull String name, String projectName) {
        this.infoWhenCredentialsAreNull = infoWhenCredentialsAreNull;
        this.name = name;
        this.url = "jdbc:sqlite:" + (path != null ? path + "/" : "") + name + ".db";
        this.projectName = projectName;
    }
    protected AbstractSqliteManager(boolean infoWhenCredentialsAreNull, @Nullable String path, @NotNull String name, HikariDataSource dataSource, String projectName) {
        this(infoWhenCredentialsAreNull, path, name, projectName);
        this.dataSource = dataSource;
    }

    /**
     * !! ONLY FOR LOGGING !!
     *
     * @param sql Sql Command with ?'s
     * @param params Parameter for ?'s in sql
     * @param cut Use shorter forms for SELECT, WHERE and FROM
     * @return SQL Command with all Parameters
     */
    public String buildSqlWithParams(String sql, @NotNull List<Object> params, boolean cut) {
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

    public @NotNull HikariConfig createHikariConfig() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(url);
        config.setDriverClassName("org.sqlite.JDBC");
        config.setMaximumPoolSize(1);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(5000);
        config.setIdleTimeout(60000);
        config.setMaxLifetime(1800000);
        config.addDataSourceProperty("autoReconnect", true);
        config.addDataSourceProperty("useSSL", false);

        return config;
    }
    public void connect() {
        if(isClosed())
            throw new IllegalStateException(projectName + " executed after disable! Manager already closed");

        if (url == null || url.isBlank()) {
            if(infoWhenCredentialsAreNull) {
                DBUtils.logging(DebugLevel.LEVEL1, projectName).warning(
                        "url was null while connecting Sqlite for Project: " + projectName +
                                " url=null: " + (url == null)
                );
                DBUtils.logging(DebugLevel.LEVEL0, projectName).logException(new NullPointerException(
                        "url was null while connecting Sqlite for Project: " + projectName +
                                " url=null: " + (url == null)
                ));
            }
            return;
        }
        long start = System.currentTimeMillis();

        HikariConfig config = createHikariConfig();
        dataSource = new HikariDataSource(config);

        DBUtils.logging(DebugLevel.LEVEL3, projectName).info("Connected to '" + config.getJdbcUrl() + "' in " + (System.currentTimeMillis() - start) + " ms");
    }
    public void disconnect() {
        long start = System.currentTimeMillis();
        if(dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            dataSource = null;
            DBUtils.logging(DebugLevel.LEVEL3, projectName).info("Disconnected from '" + url + "' in " + (System.currentTimeMillis() - start) + " ms");
        } else
            DBUtils.logging(DebugLevel.LEVEL1, projectName).info("Couldn't disconnect from '" + url + "': Not connected");
    }
    public void checkConnection() {
        if (isClosed())
            throw new IllegalStateException(projectName + " executed after disable! Manager already closed");

        if (getDataSource() == null || getDataSource().isClosed()) {
            connect();
            if (getDataSource() == null || getDataSource().isClosed())
                DBUtils.logging(DebugLevel.LEVEL1, projectName).error("Error after trying to connect to database: Cant connect!");
        }
    }

    public Connection getConnection() {
        checkConnection();
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            DBUtils.logging(DebugLevel.LEVEL1, projectName).error("Error while getConnection: " + e.getMessage());
            DBUtils.logging(DebugLevel.LEVEL0, projectName).logException(e);
            return null;
        }
    }

    public AsyncSqliteManager async() {
        return new AsyncSqliteManager(infoWhenCredentialsAreNull, name, url, dataSource, projectName);
    }
    public SqliteManager sync() {
        return new SqliteManager(infoWhenCredentialsAreNull, name, url, dataSource, projectName);
    }
}
