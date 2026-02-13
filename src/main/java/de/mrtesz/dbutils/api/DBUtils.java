package de.mrtesz.dbutils.api;

import de.mrtesz.dbutils.api.db.manager.SyncDBManager;
import de.mrtesz.dbutils.utils.db.manager.mariadb.MariaDBManager;
import de.mrtesz.dbutils.utils.db.manager.sqlite.SqliteManager;
import de.mrtesz.dbutils.utils.exceptions.DatabaseException;
import de.mrtesz.dbutils.utils.init.Init;
import de.mrtesz.dbutils.utils.logger.api.DBLogger;
import de.mrtesz.dbutils.utils.logger.api.DebugLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

@SuppressWarnings("unused")
public class DBUtils {

    @Getter
    private static DBUtils instance;

    private final Init init;

    public DBUtils(@Nullable Logger javaLogger) {
        this.init = new Init(javaLogger);

        instance = this;
    }

    public static void initialize() {
        new DBUtils(null);
    }
    public static void initialize(@Nullable Logger javaLogger) {
        new DBUtils(javaLogger);
    }

    /** Logs an exception */
    public void logException(@NotNull Throwable throwable) {
        init.getLogger(DebugLevel.LEVEL0).logException(throwable);
    }

    /**
     * Creates a {@link DBLogger}
     * @param debugLevel Level of Logging used for your Message
     */
    public DBLogger getLogger(@NotNull DebugLevel debugLevel) {
        return init.getLogger(debugLevel);
    }

    /** Creates a {@link DBLogger} with project name for specific logs */
    public DBLogger getLogger(@NotNull DebugLevel debugLevel, @Nullable String projectName) {
        return init.getLogger(debugLevel, projectName);
    }

    /** static {@link #getLogger(DebugLevel)} */
    public static DBLogger logging(@NotNull DebugLevel debugLevel) {
        return getInstance().getLogger(debugLevel);
    }
    /** static {@link #getLogger(DebugLevel, String)} */
    public static DBLogger logging(@NotNull DebugLevel debugLevel, @Nullable String projectName) {
        return getInstance().getLogger(debugLevel, projectName);
    }

    /**
     * Creates a {@link MariaDBManager} for your project
     * @param projectName Name of the project using the method
     * @param infoWhenCredentialsAreNull throw an error, if url, user or password is null
     * @param name Name of database wich is used, when null "Main"
     * @param url Url for the Database
     * @param user User for the Database
     * @param password Password for the Database
     * @return A MariaDBManager
     */
    public SyncDBManager createMariaDBManager(@NotNull String projectName, boolean infoWhenCredentialsAreNull, @Nullable String name,
                                              @Nullable String url, @Nullable String user, @Nullable String password) throws DatabaseException {
        return new MariaDBManager(infoWhenCredentialsAreNull, name, url, user, password, null, projectName);
    }

    /**
     * Creates a SqliteManager for your Project
     * @param projectName The name of the Project using the method
     * @param path Path to the .db file
     * @param name Name of the .db file
     * @return A MariaDBManager
     */
    public SyncDBManager createSqliteManager(@NotNull String projectName, @Nullable String path,
                                             @NotNull String name) throws DatabaseException {
        return new SqliteManager(path, name, null, projectName);
    }
}
