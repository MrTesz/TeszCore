package io.github.mrtesz.teszcore.api;

import io.github.mrtesz.teszcore.copyable.Copyable;
import io.github.mrtesz.teszcore.db.manager.mariadb.MariaDBManager;
import io.github.mrtesz.teszcore.db.manager.sqlite.SqliteManager;
import io.github.mrtesz.teszcore.exceptions.DuplicateInitializationException;
import io.github.mrtesz.teszcore.internal.init.Init;
import io.github.mrtesz.teszcore.logger.TeszCoreLogger;
import io.github.mrtesz.teszcore.logger.TeszCoreLoggerFactory;
import io.github.mrtesz.teszcore.logger.level.DebugLevel;
import io.github.mrtesz.teszcore.util.Conditions;
import lombok.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.logging.Logger;

/** The API class **/
@SuppressWarnings("unused")
public class TeszCoreApi implements Copyable<TeszCoreApi> {

    private static TeszCoreApi instance = null;

    private final Initializer initializedWith;

    private final Init init;

    private TeszCoreApi(Initializer initializer, @Nullable Logger javaLogger, Level consoleLoggerLevel, boolean loggerFileEnabled, @NotNull String maxFilesToKeep,
                        @Nullable String loggerPath, @NotNull String loggerName, @NotNull String loggerFileName, TeszCoreLoggerFactory teszCoreLoggerFactory) {
        this.initializedWith = initializer;

        this.init = new Init(javaLogger, consoleLoggerLevel, loggerFileEnabled, maxFilesToKeep, loggerPath, loggerName, loggerFileName, teszCoreLoggerFactory);

        instance = this;
    }

    @Builder
    @ToString
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Initializer {

        /** Optional: Java {@link Logger} e.g. for Plugins. Default: null */
        @Builder.Default
        private @Nullable Logger javaLogger = null;

        /** Lowest {@link Level} of messages, logged in the console. Default: {@link Level#INFO} */
        @Builder.Default
        private @NonNull @NotNull Level consoleLoggerLevel = Level.INFO;

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
        private @NonNull @NotNull TeszCoreLoggerFactory teszCoreLoggerFactory = TeszCoreLogger::new;

        /** Should {@link TeszCoreApi#initialize(Initializer)} overwrite the current instance if initialized earlier. Default: false */
        @Builder.Default
        private boolean overwrite = false;
    }

    /**
     * Create a new {@link TeszCoreApi} instance
     * @param initializer Initializer class (previously created with the {@link Initializer#builder()})
     * @return The created {@link TeszCoreApi} instance (can also be obtained with {@link TeszCoreApi#getInstance()}
     * @throws DuplicateInitializationException when a {@link TeszCoreApi} instance was already initialized by other projects (can be bypassed with setting {@link Initializer#overwrite} to true)
     */
    public static TeszCoreApi initialize(Initializer initializer) throws DuplicateInitializationException {
        TeszCoreApi currentInstance = instance;
        if (currentInstance != null && !initializer.overwrite)
            throw new DuplicateInitializationException("Failed to initialize TeszCoreApi.java: An instance of TeszCore is already initialized. Previous Installation: " + currentInstance.getInitializedWith());

        initializer.loggerFilePath = validateAndNormalizePath(initializer.loggerFilePath);

        return new TeszCoreApi(initializer,
                initializer.javaLogger,
                initializer.consoleLoggerLevel,
                initializer.loggerFileEnabled,
                initializer.maxLoggerFilesToKeep,
                initializer.loggerFilePath,
                initializer.loggerName,
                initializer.loggerFileName,
                initializer.teszCoreLoggerFactory);
    }
    private static String validateAndNormalizePath(@Nullable String path) {
        if (path == null) return null;

        path = path.trim();
        if (path.isEmpty()) return null;

        Path normalized = Path.of(path).normalize();

        File dir = normalized.toFile();
        if (!dir.exists())
            if (!dir.mkdirs())
                if (!dir.exists()) // race condition check
                    throw new IllegalStateException("Could not create log directory: " + normalized);

        if (!dir.isDirectory())
            throw new IllegalArgumentException("Log path is not a directory: " + normalized);
        if (!dir.canWrite())
            throw new IllegalArgumentException("Log path is not writable: " + normalized);

        return normalized.toString().replace("\\", "/");
    }

    /**
     * Logs a throwable by calling {@link TeszCoreLogger#printStackTrace}
     * @param throwable The throwable to log
     */
    public void logException(@NotNull Throwable throwable) {
        init.getLogger(DebugLevel.LEVEL0).printStackTrace(throwable);
    }

    /**
     * Creates a {@link TeszCoreLogger}
     * @param debugLevel Level of Logging used for your Message
     * @return a new TeszCoreLogger
     */
    public TeszCoreLogger getLogger(@NotNull DebugLevel debugLevel) {
        return init.getLogger(debugLevel);
    }

    /**
     * Creates a {@link TeszCoreLogger} with project name for specific logs
     * @param debugLevel Level of Logging used for your Message
     * @param projectName The name of the project, using the logger
     * @return a new TeszCoreLogger
     */
    public TeszCoreLogger getLogger(@NotNull DebugLevel debugLevel, @Nullable String projectName) {
        return init.getLogger(debugLevel, projectName);
    }

    /**
     * Creates a {@link MariaDBManager} for your project
     * @param projectName Name of the project using the method
     * @param infoWhenCredentialsAreNull throw an error, if url, user or password is null/blank
     * @param name Name of the database manager Default: "Main"
     * @param url Url of the database, connecting to
     * @param user Username of the user, connecting with
     * @param password Password of the user, connecting with
     * @return a {@link MariaDBManager}
     */
    public MariaDBManager createMariaDBManager(@Nullable String projectName, boolean infoWhenCredentialsAreNull, @Nullable String name,
                                               String url, String user, String password) {
        return new MariaDBManager(infoWhenCredentialsAreNull, name, url, user, password, null, projectName);
    }
    /**
     * Creates a {@link MariaDBManager} for your project
     * @param projectName Name of the project using the method
     * @param infoWhenCredentialsAreNull throw an error, if url, user or password is null/blank
     * @param url Url of the database, connecting to
     * @param user Username of the user, connecting with
     * @param password Password of the user, connecting with
     * @return a {@link MariaDBManager}
     */
    public MariaDBManager createMariaDBManager(@Nullable String projectName, boolean infoWhenCredentialsAreNull, String url, String user, String password) {
        return new MariaDBManager(infoWhenCredentialsAreNull, null, url, user, password, null, projectName);
    }

    /**
     * Creates a {@link SqliteManager} for your Project
     * @param projectName The name of the Project using the method
     * @param path Path of the `.db` file — `null` uses the default location
     * @param fileName Name of the `.db` file
     * @return a {@link SqliteManager}
     */
    public SqliteManager createSqliteManager(@Nullable String projectName, @Nullable String path, @NotNull String fileName) {
        return new SqliteManager(path, fileName, null, projectName);
    }

    /**
     * Get the {@link org.apache.logging.log4j.Logger}, TeszCore uses internally
     * @return the {@link org.apache.logging.log4j.Logger}, TeszCore uses internally
     */
    public org.apache.logging.log4j.Logger getLog4jLogger() {
        return init.getLogger();
    }

    /**
     * Get the {@link org.apache.logging.log4j.core.LoggerContext}, the {@link #getLog4jLogger() log4j.Logger} uses
     * @return the {@link org.apache.logging.log4j.core.LoggerContext}, the {@link #getLog4jLogger() log4j.Logger} uses
     */
    public LoggerContext getLoggerContext() {
        return init.getLoggerContext();
    }

    /**
     * Get the {@link Initializer}, the API was latest initialized with
     * @return the {@link Initializer}, the API was latest initialized with
     */
    private Initializer getInitializedWith() {
        return initializedWith;
    }

    /**
     * Get the current instance
     * @return Current TeszCoreApi instance
     * @throws NullPointerException if the TeszCoreApi was not initialized before with {@link #initialize(Initializer)}
     */
    public static TeszCoreApi getInstance() throws NullPointerException {
        Conditions.checkNonNull(instance, "TeszCoreApi was not initialized. Initialize the TeszCoreApi with TeszCoreApi#initialize");
        return instance;
    }

    @Override
    public TeszCoreApi copy() throws DuplicateInitializationException, IllegalArgumentException {
        return initialize(initializedWith);
    }
}
