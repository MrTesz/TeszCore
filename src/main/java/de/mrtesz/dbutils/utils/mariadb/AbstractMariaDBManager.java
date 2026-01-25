package de.mrtesz.dbutils.utils.mariadb;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.mrtesz.dbutils.api.DBUtilsApi;
import lombok.Getter;
import de.mrtesz.dbutils.utils.logger.DebugLevel;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static de.mrtesz.dbutils.api.DBUtilsApi.logging;

@SuppressWarnings("unused")
public abstract class AbstractMariaDBManager {

    @Getter private boolean closed = false;
    @Getter private HikariDataSource dataSource;
    private final boolean infoWhenCredentialsAreNull;
    @Getter private final String name;
    private final String url;
    private final String user;
    private final String password;
    private final String projectName;

    protected AbstractMariaDBManager(boolean infoWhenCredentialsAreNull, String name, String url, String user, String password, String projectName) {
        this.infoWhenCredentialsAreNull = infoWhenCredentialsAreNull;
        this.name = name == null ? "Main" : name;
        this.url = url;
        this.user = user;
        this.password = password;
        this.projectName = projectName;
    }
    protected AbstractMariaDBManager(boolean infoWhenCredentialsAreNull,
                                     String name, String url, String user, String password, HikariDataSource dataSource, String projectName) {
        this(infoWhenCredentialsAreNull, name, url, user, password, projectName);
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
        config.setUsername(user);
        config.setPassword(password);
        config.setDriverClassName("org.mariadb.jdbc.Driver");
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(5000);
        config.setIdleTimeout(60000);
        config.setMaxLifetime(1800000);
        config.addDataSourceProperty("autoReconnect", true);
        config.addDataSourceProperty("useSSL", false);
        return config;
    }
    public void connect() {
        if(closed)
            throw new IllegalStateException(projectName + " executed after disable! Manager already closed");

        if ((url == null || user == null || password == null)) {
            if(infoWhenCredentialsAreNull) {
                DBUtilsApi.logging(DebugLevel.LEVEL1, projectName).warning(
                        "url, user or password were null while connecting MariaDB for Project: " + projectName +
                                " url=null: " + (url == null) +
                                " user=null: " + (user == null) +
                                " password=null: " + (password == null)
                );
                DBUtilsApi.logging(DebugLevel.LEVEL0, projectName).logException(new NullPointerException(
                        "url, user or password were null while connecting MariaDB for Project: " + projectName +
                                " url=null: " + (url == null) +
                                " user=null: " + (user == null) +
                                " password=null: " + (password == null)
                ));
            }
            return;
        }
        long start = System.currentTimeMillis();

        HikariConfig config = createHikariConfig();
        dataSource = new HikariDataSource(config);

        DBUtilsApi.logging(DebugLevel.LEVEL3, projectName).info("Connected to '" + config.getJdbcUrl() + "' in " + (System.currentTimeMillis() - start) + " ms");
    }
    public void disconnect() {
        long start = System.currentTimeMillis();
        if(dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            dataSource = null;
            DBUtilsApi.logging(DebugLevel.LEVEL3, projectName).info("Disconnected from '" + url + "' in " + (System.currentTimeMillis() - start) + " ms");
        } else
            DBUtilsApi.logging(DebugLevel.LEVEL1, projectName).info("Couldn't disconnect from '" + url + "': Not connected");
    }
    public void checkConnection() {
        if (isClosed())
            throw new IllegalStateException(projectName + " executed after disable! Manager already closed");

        if (getDataSource() == null || getDataSource().isClosed()) {
            connect();
            if (getDataSource() == null || getDataSource().isClosed())
                DBUtilsApi.logging(DebugLevel.LEVEL1, projectName).error("Error after trying to connect to database: Cant connect!");
        }
    }

    public void close() {
        this.closed = true;
    }

    public Connection getConnection() {
        checkConnection();
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            DBUtilsApi.logging(DebugLevel.LEVEL1, projectName).error("Error while getConnection: " + e.getMessage());
            DBUtilsApi.logging(DebugLevel.LEVEL0, projectName).logException(e);
            return null;
        }
    }

    public AsyncMariaDBManager async() {
        return new AsyncMariaDBManager(infoWhenCredentialsAreNull, name, url, user, password, dataSource, projectName);
    }
    public MariaDBManager sync() {
        return new MariaDBManager(infoWhenCredentialsAreNull, name, url, user, password, dataSource, projectName);
    }
}
