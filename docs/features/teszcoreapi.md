# TeszCoreAPI
###### Last updated for version: 2.4.1

## Usage

The `TeszCoreAPI` class can be used for:
- Initializing the API instance
- Logger creation (See [Logging](logging))
- Database manager creation (See [Database Managing](database-managing))

## Initialize the API instance

Most of the classes in this project need an initialized `TeszCoreAPI` class, to get the logger from it.
Therefore, it's smart to initialize a `TeszCoreAPI` instance, when initializing your project. 

To create a new `TeszCoreAPI` instance you have to use the `TeszCoreAPI.Initializer`:
```java
TeszCoreApi.initialize(
        // Create a new builder
        TeszCoreApi.Initializer.builder()
                .loggerName("MyProjectLogger") // name of the logger
                .loggerFileName("MyProject") // name of the file, the logs are written to
                .loggerFilePath("logs") // path to the log file
                .build()
);
```

**Initializer values:**

|          Value          |        Default        | Description                                                                                                                                                                                                                     |
|:-----------------------:|:---------------------:|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|       `overwrite`       |        `false`        | `true`: `TeszCoreApi#initialize` overwrites the current instance if already initialized before <br>`false`: throw a `DuplicateInitializationException` when initialized a second time                                           | 
| `teszCoreLoggerFactory` | `TeszCoreLogger::new` | Factory called when using `TeszCoreApi#getLogger`                                                                                                                                                                               |
|      `javaLogger`       |        `null`         | If not null, log messages will be written into this logger too                                                                                                                                                                  |
|  `consoleLoggerLevel`   |     `Level.INFO`      | Lowest level of logger messages, displayed in the console                                                                                                                                                                       |
|      `loggerName`       |  `"TeszCoreLogger"`   | The name of the Logger. Represents the <abbr title="org.apache.logging.log4j.core.appender.AbstractAppender">`AbstractAppender.Builder#name`</abbr>                                                                             |
|   `loggerFileEnabled`   |        `true`         | If the logs should be written in a .log file                                                                                                                                                                                    |
|    `loggerFilePath`     |        `null`         | *Ignored if `loggerFileEnabled` is `false`* - Path of the logger file                                                                                                                                                           |
|    `loggerFileName`     |     `"TeszCore"`      | *Ignored if `loggerFileEnabled` is `false`* - The name of the logger file                                                                                                                                                       |
| `maxLoggerFilesToKeep`  |        `"10"`         | *Ignored if `loggerFileEnabled` is `false`* - Max amount of old logger files to keep, older files will be deleted<br/>**Why String?**<br/> The logger uses log4j and it's `DefaultRolloverStrategy.Builder#withMax(String max)` |

