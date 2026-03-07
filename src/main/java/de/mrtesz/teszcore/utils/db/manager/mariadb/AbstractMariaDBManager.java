package de.mrtesz.teszcore.utils.db.manager.mariadb;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.mrtesz.teszcore.api.TeszCoreApi;
import de.mrtesz.teszcore.api.db.manager.AsyncDBManager;
import de.mrtesz.teszcore.api.db.manager.SyncDBManager;
import de.mrtesz.teszcore.utils.db.manager.AbstractDBManager;
import de.mrtesz.teszcore.utils.logger.level.DebugLevel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractMariaDBManager extends AbstractDBManager {

    protected final boolean infoWhenCredentialsAreNull;
    protected final @Getter @NotNull String name;
    protected final String url;
    protected final String user;
    protected final String password;
    protected @Getter HikariDataSource dataSource;
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
    public void connect() {
        if(isClosed())
            throw new IllegalStateException(projectName + " executed after disable! Manager already closed");

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
    public void checkConnection() {
        if (isClosed())
            throw new IllegalStateException(projectName + " executed after disable! Manager already closed");

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
            dataSource.close();
            dataSource = null;
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
            TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL0, projectName).logException(e);
            return null;
        }
    }

    /**
     * Creates an {@link AsyncMariaDBManager} with the existing arguments of the object calling this method
     * @param timeoutSecs References the {@link AsyncMariaDBManager#timeoutSeconds}
     */
    public AsyncDBManager async(int timeoutSecs) {
        return new de.mrtesz.teszcore.utils.db.manager.mariadb.AsyncMariaDBManager(infoWhenCredentialsAreNull, name, url, user, password, dataSource, projectName, timeoutSecs);
    }

    /**
     * Creates a {@link MariaDBManager} with the existing arguments of the object calling this method
     */
    public SyncDBManager sync() {
        return new de.mrtesz.teszcore.utils.db.manager.mariadb.MariaDBManager(infoWhenCredentialsAreNull, name, url, user, password, dataSource, projectName);
    }
}
