package de.mrtesz.dbutils.utils.db.manager.sqlite;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.mrtesz.dbutils.api.DBUtils;
import de.mrtesz.dbutils.api.db.manager.AsyncDBManager;
import de.mrtesz.dbutils.api.db.manager.SyncDBManager;
import de.mrtesz.dbutils.utils.db.manager.AbstractDBManager;
import de.mrtesz.dbutils.utils.logger.api.DebugLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractSqliteManager extends AbstractDBManager {

    @Getter
    private HikariDataSource dataSource;
    @Getter
    private final @NotNull String name;
    private final String url;
    private final String projectName;

    protected AbstractSqliteManager(@Nullable String path, @NotNull String name, @Nullable HikariDataSource dataSource, @Nullable String projectName) {
        if (name.isBlank()) throw new IllegalArgumentException("name param of " + AbstractSqliteManager.class.getName() + " was blank!");

        this.name = name;
        this.url = "jdbc:sqlite:" + (path != null ? path + "/" : "") + name + ".db";
        this.projectName = projectName;
        this.dataSource = dataSource;
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

    @Override
    public void connect() {
        if(isClosed())
            throw new IllegalStateException(projectName + " executed after disable! Manager already closed");

        long start = System.currentTimeMillis();

        HikariConfig config = createHikariConfig();
        dataSource = new HikariDataSource(config);

        DBUtils.getInstance().getLogger(DebugLevel.LEVEL3, projectName).info("Connected to '" + config.getJdbcUrl() + "' in " + (System.currentTimeMillis() - start) + " ms");
    }

    @Override
    public void disconnect() {
        long start = System.currentTimeMillis();

        if(dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            dataSource = null;
            DBUtils.getInstance().getLogger(DebugLevel.LEVEL3, projectName).info("Disconnected from '" + url + "' in " + (System.currentTimeMillis() - start) + " ms");
        } else
            DBUtils.getInstance().getLogger(DebugLevel.LEVEL1, projectName).info("Couldn't disconnect from '" + url + "': Not connected");
    }

    @Override
    public void checkConnection() {
        if (isClosed())
            throw new IllegalStateException(projectName + " executed after disable! Manager already closed");

        if (getDataSource() == null || getDataSource().isClosed()) {
            connect();
            if (getDataSource() == null || getDataSource().isClosed())
                DBUtils.getInstance().getLogger(DebugLevel.LEVEL1, projectName).error("Error after trying to connect to database: Cant connect!");
        }
    }

    @Override
    public Connection getConnection() {
        checkConnection();
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            DBUtils.getInstance().getLogger(DebugLevel.LEVEL1, projectName).error("Error while getConnection: " + e.getMessage());
            DBUtils.getInstance().getLogger(DebugLevel.LEVEL0, projectName).logException(e);
            return null;
        }
    }

    /**
     * Creates an {@link AsyncSqliteManager} with the existing arguments of the object calling this method
     * @param timeoutSecs References the {@link AsyncSqliteManager#timeoutSeconds}
     */
    @Override
    public AsyncDBManager async(int timeoutSecs) {
        return new AsyncSqliteManager(name, url, dataSource, projectName, timeoutSecs);
    }

    /**
     * Creates a {@link SqliteManager} with the existing arguments of the object calling this method
     */
    @Override
    public SyncDBManager sync() {
        return new SqliteManager(name, url, dataSource, projectName);
    }
}
