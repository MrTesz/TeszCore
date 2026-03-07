package de.mrtesz.dbutils.utils.logger;

import de.mrtesz.dbutils.utils.logger.level.DebugLevel;
import org.apache.logging.log4j.Logger;

public interface DBLoggerFactory {
    DBLogger create(Logger logger, DebugLevel debugLevel, String projectName);
}
