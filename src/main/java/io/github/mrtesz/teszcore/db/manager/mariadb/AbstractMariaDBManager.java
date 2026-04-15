package io.github.mrtesz.teszcore.db.manager.mariadb;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.mrtesz.teszcore.api.TeszCoreApi;
import io.github.mrtesz.teszcore.api.db.manager.AsyncDBManager;
import io.github.mrtesz.teszcore.api.db.manager.SyncDBManager;
import io.github.mrtesz.teszcore.db.manager.AbstractDBManager;
import io.github.mrtesz.teszcore.logger.level.DebugLevel;
import io.github.mrtesz.teszcore.util.Conditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractMariaDBManager extends AbstractDBManager {

    protected final boolean infoWhenCredentialsAreNull;
    @Getter
    protected final @NotNull String name;
    protected final String url;
    protected final String user;
    protected final String password;
    @Getter
    protected HikariDataSource dataSource;
    protected final String projectName;

    protected @NotNull HikariConfig createHikariConfig() {
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

    @Override
    public void connect() throws IllegalStateException, NullPointerException {
        Conditions.checkState(!isClosed(), projectName + " executed after disable! Manager already closed");

        if (url == null || user == null || password == null || url.isBlank() || user.isBlank() || password.isBlank()) {
            if(infoWhenCredentialsAreNull)
                throw new NullPointerException(
                        "url, user or password were null while connecting MariaDB for Project: " + projectName +
                                " url=null: " + (url == null) +
                                " user=null: " + (user == null) +
                                " password=null: " + (password == null)
                );
            return;
        }
        long start = System.currentTimeMillis();

        HikariConfig config = createHikariConfig();
        dataSource = new HikariDataSource(config);

        TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL3, projectName).info("Connected to '" + config.getJdbcUrl() + "' in " + (System.currentTimeMillis() - start) + " ms");
    }
    @Override
    public void checkConnection() throws IllegalStateException {
        Conditions.checkState(!isClosed(), projectName + " executed after disable! Manager already closed");

        if (getDataSource() == null || getDataSource().isClosed()) {
            connect();
            if (getDataSource() == null || getDataSource().isClosed())
                TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL1, projectName).error("Error after trying to connect to database: Cant connect!");
        }
    }

    @Override
    public void disconnect() {
        long start = System.currentTimeMillis();
        if(dataSource != null && !dataSource.isClosed()) {
            try {
                getConnection().close();
                dataSource.close();
            } catch (SQLException e) {
                TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL1, projectName).error("Error while disconnect: " + e.getMessage());
                TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL0, projectName).printStackTrace(e);
            }
            TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL3, projectName).info("Disconnected from '" + url + "' in " + (System.currentTimeMillis() - start) + " ms");
        } else
            TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL1, projectName).info("Couldn't disconnect from '" + url + "': Not connected");
    }

    @Override
    public Connection getConnection() {
        checkConnection();
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL1, projectName).error("Error while getConnection: " + e.getMessage());
            TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL0, projectName).printStackTrace(e);
            return null;
        }
    }

    /**
     * Creates an {@link AsyncMariaDBManager} with the existing arguments of the object calling this method
     * @param timeoutSecs References the {@link AsyncMariaDBManager#timeoutSeconds}
     */
    @Override
    public AsyncDBManager async(int timeoutSecs) {
        return new AsyncMariaDBManager(infoWhenCredentialsAreNull, name, url, user, password, dataSource, projectName, timeoutSecs);
    }

    /**
     * Creates a {@link MariaDBManager} with the existing arguments of the object calling this method
     */
    public SyncDBManager sync() {
        return new MariaDBManager(infoWhenCredentialsAreNull, name, url, user, password, dataSource, projectName);
    }
}
