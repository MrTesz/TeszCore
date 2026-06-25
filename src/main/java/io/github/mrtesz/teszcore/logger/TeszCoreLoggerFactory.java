package io.github.mrtesz.teszcore.logger;

import io.github.mrtesz.teszcore.api.TeszCoreApi;
import io.github.mrtesz.teszcore.logger.level.DebugLevel;
import org.apache.logging.log4j.Logger;

/// Factory used in {@link TeszCoreApi#getLogger} methods
public interface TeszCoreLoggerFactory {
    /// Creates a TeszCoreLogger
    TeszCoreLogger create(Logger logger, DebugLevel debugLevel, String projectName);
}
