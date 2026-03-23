package io.github.mrtesz.teszcore.internal.init;

import io.github.mrtesz.teszcore.logger.TeszCoreLogger;
import io.github.mrtesz.teszcore.logger.TeszCoreLoggerFactory;
import io.github.mrtesz.teszcore.logger.level.DebugLevel;
import io.github.mrtesz.teszcore.logger.log4j.AnsiConsoleAppender;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.CompositeTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.OnStartupTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class Init {

    private final Logger logger;
    private final @Nullable java.util.logging.Logger javaLogger;

    @Setter
    private TeszCoreLoggerFactory teszCoreLoggerFactory;

    public Init(@Nullable java.util.logging.Logger javaLogger, Level consoleLoggerLevel, boolean loggerFileEnabled, @NotNull String maxFilesToKeep,
                @Nullable String loggerPath, @NotNull String loggerName, @NotNull String loggerFileName, TeszCoreLoggerFactory teszCoreLoggerFactory) {
        this.javaLogger = javaLogger;
        setDefaultUncaughtExceptionHandler();

        logger = setupLogger(loggerName, loggerPath == null ? "" : loggerPath, loggerFileName, loggerFileEnabled, maxFilesToKeep, consoleLoggerLevel);

        this.teszCoreLoggerFactory = teszCoreLoggerFactory;
    }

    @SuppressWarnings("SameParameterValue")
    private Logger setupLogger(@NotNull String name, @Nullable String path, @NotNull String fileName,
                               boolean fileEnabled, @NotNull String maxFilesToKeep, Level consoleLoggerLevel) {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();

        RollingFileAppender fileAppender = null;
        if (fileEnabled) {
            PatternLayout fileLayout = PatternLayout.newBuilder()
                    .withPattern("[%d{HH:mm:ss}] [%level] %msg%n")
                    .withConfiguration(config)
                    .build();
            fileAppender = RollingFileAppender.newBuilder()
                    .withFileName(path + "latest-" + fileName + ".log")
                    .withFilePattern(path + "%d{yyyy-MM-dd}-" + fileName + "-%i.log.gz")
                    .withPolicy(CompositeTriggeringPolicy.createPolicy(
                            OnStartupTriggeringPolicy.createPolicy(1),
                            SizeBasedTriggeringPolicy.createPolicy("10MB")
                    ))
                    .withStrategy(DefaultRolloverStrategy.newBuilder()
                            .withMax(maxFilesToKeep)
                            .withConfig(config)
                            .build())
                    .withAppend(true)
                    .setName(name + "File")
                    .setLayout(fileLayout)
                    .setConfiguration(config)
                    .build();
            fileAppender.start();
            config.addAppender(fileAppender);
        }

        PatternLayout consoleLayout = PatternLayout.newBuilder()
                .withPattern("%msg%n")
                .withConfiguration(config)
                .build();
        AbstractAppender consoleAppender = new AnsiConsoleAppender(name + "Console", consoleLayout, null, javaLogger);
        consoleAppender.start();
        config.addAppender(consoleAppender);

        AppenderRef fileRef = null;
        if (fileEnabled) fileRef = AppenderRef.createAppenderRef(name + "File", null, null);
        AppenderRef consoleRef = AppenderRef.createAppenderRef(name + "Console", consoleLoggerLevel, null);

        LoggerConfig loggerConfig = LoggerConfig.createLogger(
                false, Level.DEBUG, name, "true",
                (fileEnabled ? new AppenderRef[]{fileRef, consoleRef} : new AppenderRef[]{consoleRef}),
                null, config, null
        );

        if (fileEnabled) loggerConfig.addAppender(fileAppender, Level.DEBUG, null);
        loggerConfig.addAppender(consoleAppender, Level.INFO, null);

        config.addLogger(name, loggerConfig);
        context.updateLoggers();

        return LogManager.getLogger(name);
    }

    private void setDefaultUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> getLogger(DebugLevel.LEVEL0).logException(throwable));
    }

    public TeszCoreLogger getLogger(DebugLevel level) {
        return teszCoreLoggerFactory.create(logger, level, null);
    }
    public TeszCoreLogger getLogger(DebugLevel level, String projectName) {
        return teszCoreLoggerFactory.create(logger, level, projectName);
    }
}
