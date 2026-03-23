package io.github.mrtesz.teszcore.logger.log4j;

import io.github.mrtesz.ansi_impl.AnsiParser;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.logging.Logger;

public class AnsiConsoleAppender extends AbstractAppender {

    private final Logger javaLogger;

    public AnsiConsoleAppender(String name, Layout<? extends Serializable> layout, Filter filter, @Nullable Logger javaLogger) {
        super(name, filter, layout);
        this.javaLogger = javaLogger;
    }

    @Override
    public void append(LogEvent event) {
        try {
            String msg = event.getMessage().getFormattedMessage();

            msg = AnsiParser.replaceParagraphAnsi(msg);

            if (javaLogger != null)
                javaLogger.info(msg);
            else
                System.out.println(msg);
        } catch (Exception ex) {
            throw new AppenderLoggingException(ex);
        }
    }
}
