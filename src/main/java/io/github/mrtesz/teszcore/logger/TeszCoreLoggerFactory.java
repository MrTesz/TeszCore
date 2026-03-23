package io.github.mrtesz.teszcore.logger;

import io.github.mrtesz.teszcore.logger.level.DebugLevel;
import org.apache.logging.log4j.Logger;

public interface TeszCoreLoggerFactory {
    TeszCoreLogger create(Logger logger, DebugLevel debugLevel, String projectName);
}
