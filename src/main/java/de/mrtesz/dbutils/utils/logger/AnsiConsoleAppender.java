package de.mrtesz.dbutils.utils.logger;

import de.mrtesz.ansi.AnsiParser;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;

import java.io.Serializable;

public class AnsiConsoleAppender extends AbstractAppender {

    public AnsiConsoleAppender(String name, Layout<? extends Serializable> layout, Filter filter) {
        super(name, filter, layout);
    }

    @Override
    public void append(LogEvent event) {
        try {
            String msg = event.getMessage().getFormattedMessage();

            msg = AnsiParser.replaceParagraphAnsi(msg);

            System.out.println(msg);
        } catch (Exception ex) {
            throw new AppenderLoggingException(ex);
        }
    }
}
