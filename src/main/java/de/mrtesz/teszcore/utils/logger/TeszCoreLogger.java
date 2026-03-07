package de.mrtesz.teszcore.utils.logger;

import de.mrtesz.teszcore.utils.copyable.Copyable;
import de.mrtesz.teszcore.utils.logger.level.DebugLevel;
import de.mrtesz.teszcore.utils.logger.level.LoggerLevel;
import lombok.NonNull;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

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

    public void logException(Throwable throwable) {
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
        printStackTrace(throwable);
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

    public void log(Level level, String msg) {
        if (level == Level.ERROR) error(msg);
        else if (level == Level.WARN) warning(msg);
        else if (level == Level.INFO) info(msg);
        else if (level == Level.DEBUG) debug(msg);
    }

    public void debug(String msg) {
        logger.debug("{}[{}] {}", (projectName != null ? "[" + projectName + "] " : ""), lvl, msg);
    }

    public void info(String msg) {
        if (lvl >= 8)
            debug(msg);
        else
            logger.info("{}[{}] {}", (projectName != null ? "[" + projectName + "] " : ""), lvl, msg);
    }

    public void warning(String msg) {
        logger.warn("{}[{}] {}", (projectName != null ? "[" + projectName + "] " : ""), lvl, msg);
    }

    public void error(String msg) {
        logger.error("{}[{}] {}", (projectName != null ? "[" + projectName + "] " : ""), lvl, msg);
    }

    public void throwing(Throwable t) {
        logger.throwing(t);
    }

    public void printStackTrace(Throwable t) {
        logger.throwing(t);
    }

    public void setLevel(DebugLevel level) {
        this.level = level;
        this.lvl = level.getIntValue();
    }

    @Override
    public TeszCoreLogger copy() {
        return new TeszCoreLogger(this.logger, this.level, this.projectName);
    }
}
