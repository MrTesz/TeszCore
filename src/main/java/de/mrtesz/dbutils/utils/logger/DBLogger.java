package de.mrtesz.dbutils.utils.logger;

import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class DBLogger {

    private final DebugLevel level;
    private final int lvl;
    private final Logger logger;
    private final @Nullable String projectName;

    public DBLogger(Logger logger, DebugLevel level, @Nullable String projectName) {
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
            case INFO -> info(msg);
            case DEBUG -> debug(msg);
            case WARNING -> warning(msg);
            case ERROR -> error(msg);
        }
    }

    public void debug(String msg) {
        if (lvl < 11)
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

    public void printStackTrace(Throwable t) {
        logger.throwing(t);
    }
}
