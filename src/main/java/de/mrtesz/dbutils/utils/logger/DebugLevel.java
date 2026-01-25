package de.mrtesz.dbutils.utils.logger;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum DebugLevel {

    LEVEL11(11, LoggerLevel.DEBUG, "column exists", "index exists"),
    LEVEL10(10, LoggerLevel.DEBUG, "insert", "update", "select", "remove"),
    LEVEL9(9, LoggerLevel.DEBUG),
    LEVEL8(8, LoggerLevel.DEBUG, "create", "alter"),
    LEVEL5(5, LoggerLevel.INFO, "create/close MariaDBManagers", "Initializing Projects"),
    LEVEL3(3, LoggerLevel.INFO, "connect", "disconnect"),
    LEVEL1(1, LoggerLevel.WARNING, "Warning Messages"),
    LEVEL0(0, LoggerLevel.ERROR, "Error StackTraces"),
    LEVELMINUS1(-1, LoggerLevel.ERROR, "Nothing");

    private final int intValue;
    private final List<String> uses;
    private final LoggerLevel loggerLevel;

    DebugLevel(int intValue, LoggerLevel loggerLevel, String... uses) {
        this.intValue = intValue;
        this.loggerLevel = loggerLevel;
        this.uses = Arrays.stream(uses).toList();
    }
}
