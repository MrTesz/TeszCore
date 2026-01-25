package de.mrtesz.dbutils.utils;

import de.mrtesz.dbutils.utils.config.ConfigManager;
import de.mrtesz.dbutils.utils.logger.DBLogger;
import de.mrtesz.dbutils.utils.logger.DebugLevel;
import lombok.Getter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.CompositeTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.OnStartupTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

@Getter
public class Init {

    private final Logger logger;
    private final ConfigManager configManager;

    private GeneralManager generalManager;

    public Init() {
        logger = setupLogger("DBUtilsLogger", "logs/", "DBUtils");

        setDefaultUncaughtExceptionHandler();

        this.configManager = new ConfigManager("config");

        registerManagers();
    }

    @SuppressWarnings("SameParameterValue")
    private Logger setupLogger(String name, String path, String fileName) {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();

        PatternLayout fileLayout = PatternLayout.newBuilder()
                .withPattern("[%d{HH:mm:ss}] [%level] %msg%n")
                .withConfiguration(config)
                .build();
        RollingFileAppender fileAppender = RollingFileAppender.newBuilder()
                .withFileName(path + "latest-" + fileName + ".log")
                .withFilePattern(path + "%d{yyyy-MM-dd}-" + fileName + ".log.gz")
                .withPolicy(CompositeTriggeringPolicy.createPolicy(
                        OnStartupTriggeringPolicy.createPolicy(1),
                        SizeBasedTriggeringPolicy.createPolicy("10MB")
                ))
                .withStrategy(DefaultRolloverStrategy.newBuilder()
                        .withMax("10")
                        .withConfig(config)
                        .build())
                .withAppend(true)
                .setName(name + "File")
                .setLayout(fileLayout)
                .setConfiguration(config)
                .build();
        fileAppender.start();
        config.addAppender(fileAppender);

        PatternLayout consoleLayout = PatternLayout.newBuilder()
                .withPattern("%msg%n")
                .withConfiguration(config)
                .build();
        ConsoleAppender consoleAppender = ConsoleAppender.newBuilder()
                .setName(name + "Console")
                .setTarget(ConsoleAppender.Target.SYSTEM_OUT)
                .setLayout(consoleLayout)
                .setConfiguration(config)
                .build();
        consoleAppender.start();
        config.addAppender(consoleAppender);

        AppenderRef fileRef = AppenderRef.createAppenderRef(name + "File", null, null);
        AppenderRef consoleRef = AppenderRef.createAppenderRef(name + "Console", Level.INFO, null);

        LoggerConfig loggerConfig = LoggerConfig.createLogger(
                false, Level.DEBUG, name, "true",
                new AppenderRef[]{fileRef, consoleRef}, null, config, null
        );

        loggerConfig.addAppender(fileAppender, Level.DEBUG, null);
        loggerConfig.addAppender(consoleAppender, Level.INFO, null);

        config.addLogger(name, loggerConfig);
        context.updateLoggers();

        return LogManager.getLogger(name);
    }

    private void registerManagers() {
        this.generalManager = new GeneralManager(this);
    }

    private void setDefaultUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> getLogger(DebugLevel.LEVEL0).logException(throwable));
    }

    public DBLogger getLogger(DebugLevel level) {
        return new DBLogger(logger, level, null);
    }
    public DBLogger getLogger(DebugLevel level, String projectName) {
        return new DBLogger(logger, level, projectName);
    }
}
