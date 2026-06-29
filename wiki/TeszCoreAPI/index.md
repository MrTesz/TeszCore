---
parent: TeszCore
title: TeszCoreAPI
nav_fold: false
---

# TeszCoreAPI
###### Last updated for version: 2.3.1

## Usage

The `TeszCoreAPI` class can be used for:
- Initializing the API instance
- Logger creation (See [Logging](../Logging))
- Database manager creation (See [Database Managing](../Database%20Managing))

---
## Initialize the API instance
###### [Related Bug](../Changelog/bugs/index#teszcoreapi1---exception-on-initialization-call) until version 2.0.4 (included)

Most of the classes in this project need an initialized `TeszCoreAPI` class, to get the logger from it.
Therefore, it's smart to initialize a `TeszCoreAPI` instance, when initializing your project. 

To create a new `TeszCoreAPI` instance you have to use the `TeszCoreAPI.Initializer`:
```java
TeszCoreApi.initialize(
        // Create a new builder
        TeszCoreApi.Initializer.builder()
                .loggerName("MyProjectLogger") // name of the logger
                .loggerFileName("MyProject") // name of the file, the logs are written to
                .loggerFilePath("logs/") // path to the log file
                .build()
);
```

**Initializer values:**

|          Value          |                            Type                             |                        Annotations                         |        Default        | Description                                                                                                                                                                                                                     |
|:-----------------------:|:-----------------------------------------------------------:|:----------------------------------------------------------:|:---------------------:|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|       `overwrite`       |                          `boolean`                          |                             -                              |        `false`        | `true`: `TeszCoreApi#initialize` overwrites the current instance if already initialized before <br>`false`: throw a `DuplicateInitializationException` when initialized a second time                                           | 
| `teszCoreLoggerFactory` |                   `TeszCoreLoggerFactory`                   | <abbr title="lombok.NonNull">`@NonNull`</abbr>, `@NotNull` | `TeszCoreLogger::new` | Factory called when using `TeszCoreApi#getLogger`                                                                                                                                                                               |
|      `javaLogger`       |   <abbr title="java.util.logging.Logger">`Logger`</abbr>    |                        `@Nullable`                         |        `null`         | If not null, log messages will be written into this logger too                                                                                                                                                                  |
|  `consoleLoggerLevel`   | <abbr title="org.apache.logging.log4j.Level">`Level`</abbr> | <abbr title="lombok.NonNull">`@NonNull`</abbr>, `@NotNull` |     `Level.INFO`      | Lowest level of logger messages, displayed in the console                                                                                                                                                                       |
|      `loggerName`       |                          `String`                           | <abbr title="lombok.NonNull">`@NonNull`</abbr>, `@NotNull` |  `"TeszCoreLogger"`   | The name of the Logger. Represents the <abbr title="org.apache.logging.log4j.core.appender.AbstractAppender">`AbstractAppender.Builder#name`</abbr>                                                                             |
|   `loggerFileEnabled`   |                          `boolean`                          |                             -                              |        `true`         | If the logs should be written in a .log file                                                                                                                                                                                    |
|    `loggerFilePath`     |                          `String`                           |                        `@Nullable`                         |        `null`         | *Ignored if `loggerFileEnabled` is `false`* - Path of the logger file                                                                                                                                                           |
|    `loggerFileName`     |                          `String`                           | <abbr title="lombok.NonNull">`@NonNull`</abbr>, `@NotNull` |     `"TeszCore"`      | *Ignored if `loggerFileEnabled` is `false`* - The name of the logger file                                                                                                                                                       |
| `maxLoggerFilesToKeep`  |                          `String`                           | <abbr title="lombok.NonNull">`@NonNull`</abbr>, `@NotNull` |        `"10"`         | *Ignored if `loggerFileEnabled` is `false`* - Max amount of old logger files to keep, older files will be deleted<br/>**Why String?**<br/> The logger uses log4j and it's `DefaultRolloverStrategy.Builder#withMax(String max)` |

