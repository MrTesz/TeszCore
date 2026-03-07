package de.mrtesz.teszcore.api;

import de.mrtesz.teszcore.api.db.manager.SyncDBManager;
import de.mrtesz.teszcore.utils.db.manager.mariadb.MariaDBManager;
import de.mrtesz.teszcore.utils.db.manager.sqlite.SqliteManager;
import de.mrtesz.teszcore.utils.exceptions.DatabaseException;
import de.mrtesz.teszcore.utils.exceptions.DuplicateInitializationException;
import de.mrtesz.teszcore.utils.init.Init;
import de.mrtesz.teszcore.utils.logger.TeszCoreLogger;
import de.mrtesz.teszcore.utils.logger.TeszCoreLoggerFactory;
import de.mrtesz.teszcore.utils.logger.level.DebugLevel;
import lombok.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

@SuppressWarnings("unused")
public class TeszCoreApi {

    @Getter
    private static TeszCoreApi instance = null;

    private final TeszCoreInitializer initializedWith;

    private final Init init;

    private TeszCoreApi(TeszCoreInitializer initializer, @Nullable Logger javaLogger, Level consoleLoggerLevel, boolean loggerFileEnabled, @NotNull String maxFilesToKeep,
                        @Nullable String loggerPath, @NotNull String loggerName, @NotNull String loggerFileName, TeszCoreLoggerFactory teszCoreLoggerFactory) {
        this.initializedWith = initializer;

        this.init = new Init(javaLogger, consoleLoggerLevel, loggerFileEnabled, maxFilesToKeep, loggerPath, loggerName, loggerFileName, teszCoreLoggerFactory);

        instance = this;
    }

    @Builder
    @ToString
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TeszCoreInitializer {

        /** Optional: Java {@link Logger} e.g. for Paper Plugins. Default: null */
        @Builder.Default
        private @Nullable Logger javaLogger = null;

        /** Lowest {@link Level} of messages, logged in the console. Default: {@link Level#INFO} */
        @Builder.Default
        private Level consoleLoggerLevel = Level.INFO;

        /** The name of the Logger. Represents the {@link AbstractAppender.Builder#getName()}. Default: TeszCoreLogger*/
        @Builder.Default
        private @NonNull @NotNull String loggerName = "TeszCoreLogger";

        /** If the logs should be written in a .log file. Default: true */
        @Builder.Default
        private boolean loggerFileEnabled = true;

        /** Optional: Path of the logger file. Ignored if {@link #loggerFileEnabled} is false. Default: null */
        @Builder.Default
        private @Nullable String loggerFilePath = null;

        /** Max amount of old logger files to keep, older files will be deleted. Ignored if {@link #loggerFileEnabled} is false. Default: "10" */
        @Builder.Default
        private @NonNull @NotNull String maxLoggerFilesToKeep = "10";

        /** The name of the logger file. Ignored if {@link #loggerFileEnabled} is false. Default: "TeszCore" */
        @Builder.Default
        private @NonNull @NotNull String loggerFileName = "TeszCore";

        /** The supplier called when using {@link TeszCoreApi#getLogger(DebugLevel)} or {@link TeszCoreApi#getLogger(DebugLevel, String)}. Default: TeszCoreLogger::new */
        @Builder.Default
        private TeszCoreLoggerFactory teszCoreLoggerFactory = TeszCoreLogger::new;

        /** Should {@link TeszCoreApi#initialize(TeszCoreInitializer)} overwrite the current instance if initialized earlier. Default: false */
        @Builder.Default
        private boolean overwrite = false;
    }

    /**
     * Create a new {@link TeszCoreApi} instance
     * @param teszCoreInitializer Initializer class (previously created with the {@link TeszCoreInitializer#builder()})
     * @return The created {@link TeszCoreApi} instance (can also be obtained with {@link TeszCoreApi#getInstance()}
     * @throws DuplicateInitializationException when a {@link TeszCoreApi} instance was already initialized by other projects (can be bypassed with setting {@link TeszCoreInitializer#overwrite} to true)
     * @throws IllegalArgumentException when the {@link TeszCoreInitializer#loggerFilePath} is not null and doesn't end with a '/'
     */
    public static TeszCoreApi initialize(TeszCoreInitializer teszCoreInitializer) throws DuplicateInitializationException, IllegalArgumentException {
        TeszCoreApi currentInstance = getInstance();
        if (currentInstance != null && !teszCoreInitializer.overwrite)
            throw new DuplicateInitializationException("Failed to initialize TeszCoreApi.java: An instance of TeszCore is already initialized. Previous Installation: " + currentInstance.getInitializedWith());
        if (teszCoreInitializer.loggerFilePath != null && !teszCoreInitializer.loggerFilePath.endsWith("/"))
            throw new IllegalArgumentException("path in TeszCoreApi#<init> doesn't end with '/'!");

        return new TeszCoreApi(teszCoreInitializer, teszCoreInitializer.javaLogger, teszCoreInitializer.consoleLoggerLevel, teszCoreInitializer.loggerFileEnabled,
                teszCoreInitializer.maxLoggerFilesToKeep, teszCoreInitializer.loggerFilePath, teszCoreInitializer.loggerName, teszCoreInitializer.loggerFileName, teszCoreInitializer.teszCoreLoggerFactory);
    }

    /** Logs an exception */
    public void logException(@NotNull Throwable throwable) {
        init.getLogger(DebugLevel.LEVEL0).logException(throwable);
    }

    /**
     * Creates a {@link TeszCoreLogger}
     * @param debugLevel Level of Logging used for your Message
     */
    public TeszCoreLogger getLogger(@NotNull DebugLevel debugLevel) {
        return init.getLogger(debugLevel);
    }

    /** Creates a {@link TeszCoreLogger} with project name for specific logs */
    public TeszCoreLogger getLogger(@NotNull DebugLevel debugLevel, @Nullable String projectName) {
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

    public org.apache.logging.log4j.Logger getLog4jLogger() {
        return init.getLogger();
    }

    private TeszCoreInitializer getInitializedWith() {
        return initializedWith;
    }
}
