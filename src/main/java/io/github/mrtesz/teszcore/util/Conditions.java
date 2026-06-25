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
     * Throw an error if the provided argument is `false`
     * @param argument argument
     * @throws IllegalArgumentException if {@code argument} is `false`
     */
    public static void checkArgument(boolean argument) throws IllegalArgumentException {
        if (!argument) throw new IllegalArgumentException();
    }
    /**
     * Throw an error if the provided argument is `false`
     * @param argument argument
     * @param message message in the exception, when thrown
     * @throws IllegalArgumentException if {@code argument} is `false`
     */
    public static void checkArgument(boolean argument, @Nullable String message) throws IllegalArgumentException {
        if (!argument) throw new IllegalArgumentException(message);
    }

    /**
     * Check if a number is in a range and throw an exception if not
     * @param num Number to check
     * @param min Range minimum
     * @param max Range maximum
     * @return the provided `num`
     * @throws IllegalNumberRangeException if {@code num} is lower than {@code min} or higher then {@code max}
     */
    public static <T extends Number> T checkRange(T num, T min, T max) throws IllegalNumberRangeException {
        if (num.doubleValue() < min.doubleValue()) throw new IllegalNumberRangeException(num, min, max);
        if (num.doubleValue() > max.doubleValue()) throw new IllegalNumberRangeException(num, min, max);
        return num;
    }
    /**
     * Check if a number is in a range and throw an exception if not
     * @param num Number to check
     * @param min Range minimum
     * @param max Range maximum
     * @param message message in the exception, if thrown
     * @return the provided `num`
     * @throws IllegalNumberRangeException if {@code num} is lower than {@code min} or higher then {@code max}
     */
    public static <T extends Number> T checkRange(T num, T min, T max, @Nullable String message) throws IllegalNumberRangeException {
        if (num.doubleValue() < min.doubleValue()) throw new IllegalNumberRangeException(num, min, max, message);
        if (num.doubleValue() > max.doubleValue()) throw new IllegalNumberRangeException(num, min, max, message);
        return num;
    }

    /**
     * Check a `boolean` and throw an exception if it is `false`
     * @param state state
     * @throws IllegalStateException if {@code state} is `false`
     */
    public static void checkState(boolean state) throws IllegalStateException {
        if (!state) throw new IllegalStateException();
    }
    /**
     * Check a `boolean` and throw an exception if it is `false`
     * @param state state
     * @param message message in the exception, if thrown
     *
     * @throws IllegalStateException if {@code state} is `false`
     */
    public static void checkState(boolean state, @Nullable String message) throws IllegalStateException {
        if (!state) throw new IllegalStateException(message);
    }

    /**
     * Check if an object is not `null` and throw an exception if it is
     * @param obj object
     * @throws NullPointerException if {@code obj} is null
     */
    @Contract("null -> fail; !null -> param1")
    public static <T> T checkNonNull(@Nullable T obj) throws NullPointerException {
        if (obj == null) throw new NullPointerException();
        return obj;
    }
    /**
     * Check if an object is not `null` and throw an exception if it is
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
     * Check if an object is `null` and throw an exception if it is
     * @param obj object
     * @throws NotNullException if {@code obj} is not null
     */
    @Contract("!null -> fail")
    public static <T> void checkNull(@Nullable T obj) throws NotNullException {
        if (obj != null) throw new NotNullException();
    }
    /**
     * Check if an object is `null` and throw an exception if it is
     * @param obj object
     * @param message message in the exception, when thrown
     * @throws NotNullException if {@code obj} is not null
     */
    @Contract("!null, _ -> fail")
    public static <T> void checkNull(@Nullable T obj, @Nullable String message) throws NotNullException {
        if (obj != null) throw new NotNullException(message);
    }
}
