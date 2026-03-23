package io.github.mrtesz.teszcore.logger.level;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum DebugLevel {

    LEVEL11(11, LoggerLevel.DEBUG, "column exists", "index exists"),
    LEVEL10(10, LoggerLevel.DEBUG, "insert", "update", "select", "remove"),
    LEVEL9(9, LoggerLevel.DEBUG),
    LEVEL8(8, LoggerLevel.DEBUG, "create", "alter"),
    LEVEL7(7, LoggerLevel.DEBUG),
    LEVEL6(6, LoggerLevel.INFO),
    LEVEL5(5, LoggerLevel.INFO, "Initializing classes (Initializer.class Methods)", "Logged Running (Runner.class Methods)"),
    LEVEL4(4, LoggerLevel.INFO),
    LEVEL3(3, LoggerLevel.INFO, "connect", "disconnect"),
    LEVEL2(2, LoggerLevel.WARNING, "Warning messages"),
    LEVEL1(1, LoggerLevel.ERROR, "Error messages"),
    LEVEL0(0, LoggerLevel.ERROR, "Error StackTraces"),
    LEVELMINUS1(-1, LoggerLevel.NOTHING, "Nothing");

    /// Integer value of the level
    private final int intValue;
    /// LoggerLevel used for {@link io.github.mrtesz.teszcore.logger.TeszCoreLogger#log(String)}
    private final LoggerLevel loggerLevel;
    /// Usages this level is used for in classes of this project
    private final List<String> uses;

    DebugLevel(int intValue, LoggerLevel loggerLevel, String... uses) {
        this.intValue = intValue;
        this.loggerLevel = loggerLevel;
        this.uses = Arrays.stream(uses).toList();
    }

    /**
     * Get a DebugLevel value which has the same intValue as the provided one
     * @param intValue intValue of the DebugLevel you want to get
     * @return the DebugLevel which has the intValue provided
     * @throws IllegalArgumentException when no DebugLevel has this intValue
     */
    public static DebugLevel fromIntValue(int intValue) throws IllegalArgumentException {
        return switch (intValue) {
            case 11 -> LEVEL11;
            case 10 -> LEVEL10;
            case 9 -> LEVEL9;
            case 8 -> LEVEL8;
            case 7 -> LEVEL7;
            case 6 -> LEVEL6;
            case 5 -> LEVEL5;
            case 4 -> LEVEL4;
            case 3 -> LEVEL3;
            case 2 -> LEVEL2;
            case 1 -> LEVEL1;
            case 0 -> LEVEL0;
            default -> throw new IllegalArgumentException("There is no DebugLevel with intValue" + intValue);
        };
    }
}
