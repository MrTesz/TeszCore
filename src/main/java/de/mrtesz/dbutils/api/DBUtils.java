package de.mrtesz.dbutils.api;

import de.mrtesz.dbutils.api.db.manager.SyncDBManager;
import de.mrtesz.dbutils.utils.db.manager.mariadb.MariaDBManager;
import de.mrtesz.dbutils.utils.db.manager.sqlite.SqliteManager;
import de.mrtesz.dbutils.utils.exceptions.DatabaseException;
import de.mrtesz.dbutils.utils.exceptions.DuplicateInitializationException;
import de.mrtesz.dbutils.utils.init.Init;
import de.mrtesz.dbutils.utils.logger.api.DBLogger;
import de.mrtesz.dbutils.utils.logger.api.DebugLevel;
import lombok.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

@SuppressWarnings("unused")
public class DBUtils {

    @Getter
    private static DBUtils instance = null;

    private final DBUtilsInitializer initializedWith;

    private final Init init;

    private DBUtils(DBUtilsInitializer initializer, @Nullable Logger javaLogger, Level consoleLoggerLevel, boolean loggerFileEnabled, @NotNull String maxFilesToKeep,
                    @Nullable String loggerPath, @NotNull String loggerName, @NotNull String loggerFileName) {
        this.initializedWith = initializer;

        this.init = new Init(javaLogger, consoleLoggerLevel, loggerFileEnabled, maxFilesToKeep, loggerPath, loggerName, loggerFileName);

        instance = this;
    }

    @Builder
    @ToString
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class DBUtilsInitializer {

        /** Optional: Java {@link Logger} e.g. for Paper Plugins. Default: null */
        private @Nullable Logger javaLogger;

        /** Lowest {@link Level} of messages, logged in the console. Default: {@link Level#INFO} */
        private Level consoleLoggerLevel = Level.INFO;

        /** The name of the Logger. Represents the {@link AbstractAppender.Builder#getName()} */
        private @NonNull @NotNull String loggerName = "DBUtilsLogger";

        /** If the logs should be written in a .log file. Default: true */
        private boolean loggerFileEnabled = true;

        /** Optional: Path of the logger file. Ignored if {@link #loggerFileEnabled} is false. Default: null*/
        private @Nullable String loggerFilePath;

        /** Max amount of old logger files to keep, older files will be deleted. Ignored if {@link #loggerFileEnabled} is false. Default: "10" */
        private @NonNull @NotNull String maxLoggerFilesToKeep = "10";

        /** The name of the logger file. Ignored if {@link #loggerFileEnabled} is false. Default: "DBUtils" */
        private @NonNull @NotNull String loggerFileName = "DBUtils";
    }

    /**
     * Create a new {@link DBUtils} instance
     * @param dbUtilsInitializer Initializer class (previously created with the {@link DBUtilsInitializer#builder()})
     * @return The created {@link DBUtils} instance (can also be obtained with {@link DBUtils#getInstance()}
     * @throws DuplicateInitializationException when a {@link DBUtils} instance was already initialized by other projects (can be bypassed with {@link #initializeOverwrite(DBUtilsInitializer)}
     * @throws IllegalArgumentException when the {@link DBUtilsInitializer#loggerFilePath} is not null and doesn't end with a '/'
     */
    public static DBUtils initialize(DBUtilsInitializer dbUtilsInitializer) throws DuplicateInitializationException, IllegalArgumentException {
        DBUtils currentInstance = getInstance();
        if (currentInstance != null) throw new DuplicateInitializationException("Failed to initialize DBUtils.java: An instance of DBUtils is already initialized. Previous Installation: " + currentInstance.getInitializedWith());
        if (dbUtilsInitializer.loggerFilePath != null && !dbUtilsInitializer.loggerFilePath.endsWith("/")) throw new IllegalArgumentException("path in DBUtils#<init> doesn't end with '/'!");

        return new DBUtils(dbUtilsInitializer, dbUtilsInitializer.javaLogger, dbUtilsInitializer.consoleLoggerLevel, dbUtilsInitializer.loggerFileEnabled,
                dbUtilsInitializer.maxLoggerFilesToKeep, dbUtilsInitializer.loggerFilePath, dbUtilsInitializer.loggerName, dbUtilsInitializer.loggerFileName);
    }
    /**
     * Create a new {@link DBUtils} instance
     * @param dbUtilsInitializer Initializer class (previously created with the {@link DBUtilsInitializer#builder()})
     * @return The created {@link DBUtils} instance (can also be obtained with {@link DBUtils#getInstance()}
     * @throws IllegalArgumentException when the {@link DBUtilsInitializer#loggerFilePath} is not null and doesn't end with a '/'
     */
    public static DBUtils initializeOverwrite(DBUtilsInitializer dbUtilsInitializer) throws IllegalArgumentException {
        DBUtils currentInstance = getInstance();
        if (dbUtilsInitializer.loggerFilePath != null && !dbUtilsInitializer.loggerFilePath.endsWith("/")) throw new IllegalArgumentException("path in DBUtils#<init> doesn't end with '/'!");

        return new DBUtils(dbUtilsInitializer, dbUtilsInitializer.javaLogger, dbUtilsInitializer.consoleLoggerLevel, dbUtilsInitializer.loggerFileEnabled,
                dbUtilsInitializer.maxLoggerFilesToKeep, dbUtilsInitializer.loggerFilePath, dbUtilsInitializer.loggerName, dbUtilsInitializer.loggerFileName);
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

    private DBUtilsInitializer getInitializedWith() {
        return initializedWith;
    }
}
