package io.github.mrtesz.teszcore.util;

import io.github.mrtesz.teszcore.exceptions.IllegalNumberRangeException;
import io.github.mrtesz.teszcore.exceptions.NotNullException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Conditions {

    /**
     * Check an argument
     * @param argument argument
     * @throws IllegalArgumentException if {@code argument} is false
     */
    public static void checkArgument(boolean argument) throws IllegalArgumentException {
        if (!argument) throw new IllegalArgumentException();
    }
    /**
     * Check an argument
     * @param argument argument
     * @param message message in the exception, when thrown
     * @throws IllegalArgumentException if {@code argument} is false
     */
    public static void checkArgument(boolean argument, @Nullable String message) throws IllegalArgumentException {
        if (!argument) throw new IllegalArgumentException(message);
    }

    /**
     * Check the range of a number
     * @param num the number to be checked
     * @param min the minimum value
     * @param max the maximum value
     * @throws IllegalNumberRangeException if {@code num} is lower than {@code min} or higher then {@code max}
     */
    public static <T extends Number> T checkRange(T num, T min, T max) throws IllegalNumberRangeException {
        if (num.doubleValue() < min.doubleValue()) throw new IllegalNumberRangeException(num, min, max);
        if (num.doubleValue() > max.doubleValue()) throw new IllegalNumberRangeException(num, min, max);
        return num;
    }
    /**
     * Check the range of a number
     * @param num the number to be checked
     * @param min the minimum value
     * @param max the maximum value
     * @param message message in the exception, when thrown
     * @throws IllegalNumberRangeException if {@code num} is lower than {@code min} or higher then {@code max}
     */
    public static <T extends Number> T checkRange(T num, T min, T max, @Nullable String message) throws IllegalNumberRangeException {
        if (num.doubleValue() < min.doubleValue()) throw new IllegalNumberRangeException(num, min, max, message);
        if (num.doubleValue() > max.doubleValue()) throw new IllegalNumberRangeException(num, min, max, message);
        return num;
    }

    /**
     * Check a state
     * @param state argument
     * @throws IllegalStateException if {@code state} is false
     */
    public static void checkState(boolean state) throws IllegalStateException {
        if (!state) throw new IllegalStateException();
    }
    /**
     * Check a state
     * @param state argument
     * @param message message in the exception, when thrown
     * @throws IllegalStateException if {@code state} is false
     */
    public static void checkState(boolean state, @Nullable String message) throws IllegalStateException {
        if (!state) throw new IllegalStateException(message);
    }

    /**
     * Check if an object is not null
     * @param obj object
     * @throws NullPointerException if {@code obj} is null
     */
    @Contract("null -> fail; !null -> param1")
    public static <T> T checkNonNull(@Nullable T obj) throws NullPointerException {
        if (obj == null) throw new NullPointerException();
        return obj;
    }
    /**
     * Check if an object is not null
     * @param obj object
     * @param message message in the exception, when thrown
     * @throws NullPointerException if {@code obj} is null
     */
    @Contract("null, _ -> fail; !null, _ -> param1")
    public static <T> T checkNonNull(@Nullable T obj, @Nullable String message) throws NullPointerException {
        if (obj == null) throw new NullPointerException(message);
        return obj;
    }

    /**
     * Check if an object is null
     * @param obj object
     * @throws NotNullException if {@code obj} is not null
     */
    @Contract("!null -> fail")
    public static <T> void checkNull(@Nullable T obj) throws NotNullException {
        if (obj != null) throw new NotNullException();
    }
    /**
     * Check if an object is null
     * @param obj object
     * @param message message in the exception, when thrown
     * @throws NotNullException if {@code obj} is not null
     */
    @Contract("!null, _ -> fail")
    public static <T> void checkNull(@Nullable T obj, @Nullable String message) throws NotNullException {
        if (obj != null) throw new NotNullException(message);
    }
}
