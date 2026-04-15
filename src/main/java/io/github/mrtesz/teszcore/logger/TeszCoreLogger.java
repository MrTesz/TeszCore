package io.github.mrtesz.teszcore.logger;

import io.github.mrtesz.teszcore.copyable.Copyable;
import io.github.mrtesz.teszcore.logger.level.DebugLevel;
import io.github.mrtesz.teszcore.logger.level.LoggerLevel;
import lombok.Getter;
import lombok.NonNull;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class TeszCoreLogger implements Copyable<TeszCoreLogger> {

    private DebugLevel level;
    private int lvl;
    private final Logger logger;
    private final @Nullable String projectName;

    public TeszCoreLogger(@NonNull Logger logger, @NonNull DebugLevel level, @Nullable String projectName) {
        this.logger = logger;
        this.level = level;
        this.lvl = level.getIntValue();
        this.projectName = projectName;
    }

    /**
     * Automatically logs the message provided, with the LoggerLevel provided by the given DebugLevel
     * @param msg message to log
     */
    public void log(String msg) {
        switch (level.getLoggerLevel()) {
            case LoggerLevel.INFO -> info(msg);
            case LoggerLevel.DEBUG -> debug(msg);
            case LoggerLevel.WARNING -> warning(msg);
            case LoggerLevel.ERROR -> error(msg);
        }
    }

    public void log(String msg, Level level) {
        if (level == Level.ERROR) error(msg);
        else if (level == Level.WARN) warning(msg);
        else if (level == Level.INFO) info(msg);
        else if (level == Level.DEBUG) debug(msg);
    }

    public void debug(String msg) {
        logger.debug("{}[{}] {}", (projectName != null ? "[" + projectName + "] " : ""), lvl, msg);
    }

    public void info(String msg) {
        logger.info("{}[{}] {}", (projectName != null ? "[" + projectName + "] " : ""), lvl, msg);
    }

    public void warning(String msg) {
        logger.warn("{}[{}] {}", (projectName != null ? "[" + projectName + "] " : ""), lvl, msg);
    }

    public void error(String msg) {
        logger.error("{}[{}] {}", (projectName != null ? "[" + projectName + "] " : ""), lvl, msg);
    }

    /// @see #printStackTrace(Throwable)
    public void throwing(Throwable throwable) {
        printStackTrace(throwable);
    }

    /// Log a throwable
    public void printStackTrace(Throwable throwable) {
        String type = throwable.getClass().getName();
        String message = throwable.getMessage();

        StackTraceElement[] trace = throwable.getStackTrace();
        String location = trace.length > 0
                ? trace[0].getClassName() + ":" + trace[0].getLineNumber()
                : "Unknown Location";

        Throwable cause = throwable.getCause();
        String causedBy = cause != null
                ? cause.getClass().getName() + ": " + cause.getMessage()
                : "No cause";

        String fullMessage = "§8Unhandled Exception caught!\n§cType: %s\n§cMessage: %s\n§cAt: %s\n§cCaused by: %s"
                .formatted(type, message, location, causedBy);

        error(fullMessage);
        logger.throwing(throwable);
    }

    public void setLevel(@NotNull DebugLevel level) {
        this.level = level;
        this.lvl = level.getIntValue();
    }

    @Override
    public TeszCoreLogger copy() {
        return new TeszCoreLogger(this.logger, this.level, this.projectName);
    }
}
