package de.mrtesz.teszcore.utils.logger;

import de.mrtesz.teszcore.utils.logger.level.DebugLevel;
import org.apache.logging.log4j.Logger;

public interface TeszCoreLoggerFactory {
    TeszCoreLogger create(Logger logger, DebugLevel debugLevel, String projectName);
}
