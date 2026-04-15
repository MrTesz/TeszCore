package io.github.mrtesz.teszcore.util;

import io.github.mrtesz.teszcore.exceptions.IllegalNumberRangeException;
import io.github.mrtesz.teszcore.util.tuple.Pair;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Random;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Chance {

    private static final Random RANDOM = new Random();

    /**
     * Rolls a percentage
     * @param percent percent
     * @return the rolled result
     * @throws IllegalNumberRangeException if <code>percent</code> is not in the range 0.0-1.0
     */
    public static boolean roll(double percent) throws IllegalNumberRangeException {
        Conditions.checkRange(percent, 0.0, 1.0, "'" + percent + "' in Chance#roll(double).");
        return RANDOM.nextDouble() < percent;
    }
    /**
     * Rolls a percentage with a specified random
     * @param percent percent
     * @param random random
     * @return the rolled result
     * @throws IllegalNumberRangeException if <code>percent</code> is not in the range 0.0-1.0
     * @throws NullPointerException if <code>random</code> is null
     */
    public static boolean roll(double percent, @NotNull Random random) throws IllegalNumberRangeException, NullPointerException {
        Conditions.checkRange(percent, 0.0, 1.0, "'" + percent + "' in Chance#roll(double, Random).");
        Conditions.checkNonNull(random, "NotNull 'random' is null in Chance#roll(double, Random).");
        return random.nextDouble() < percent;
    }

    /**
     * Rolls a percentage
     * @param percent percent
     * @return the rolled result
     * @throws IllegalArgumentException if <code>percent</code> is not in the range 0-100
     */
    public static boolean roll(int percent) throws IllegalNumberRangeException {
        Conditions.checkRange(percent, 0, 100, "'" + percent + "' in Chance#roll(int).");
        return roll((double) percent / 100);
    }
    /**
     * Rolls a percentage with a specified random
     * @param percent percent
     * @param random random
     * @return the rolled result
     * @throws IllegalArgumentException if <code>percent</code> is not in the range 0-100
     * @throws NullPointerException if <code>random</code> is null
     */
    public static boolean roll(int percent, @NotNull Random random) throws IllegalNumberRangeException, NullPointerException {
        Conditions.checkRange(percent, 0, 100, "'" + percent + "' in Chance#roll(int, Random).");
        Conditions.checkNonNull(random, "NotNullable object 'random' is null in Chance#roll(int, Random).");
        return roll((double) percent / 100, random);
    }

    /**
     * A weighted entry holding a chance and a result value, used in {@link Chance#roll(Collection)}.
     * The {@code chance} does not need to be normalized (e.g. sum to 1.0) —
     * entries are rolled relative to each other.
     */
    @Getter
    @ToString
    public static final class WeightedEntry<T> extends Pair<Double, T> {
        private final double chance;
        private final T value;

        public WeightedEntry(double chance, T value) {
            super(chance, value);
            this.chance = chance;
            this.value = value;
        }
    }

    /**
     * Roll a value of a weighted entry collection
     * @param entries entries
     * @return rolled value
     */
    public static <T> T roll(@NotNull Collection<? extends WeightedEntry<T>> entries) {
        double total = entries.stream()
                .mapToDouble(WeightedEntry::getChance)
                .sum();

        double roll = RANDOM.nextDouble() * total;
        double current = 0;

        for (WeightedEntry<T> entry : entries) {
            current += entry.getChance();
            if (roll < current)
                return entry.getValue();
        }

        return null;
    }
    /**
     * Roll a value of a weighted entry collection with a specified random
     * @param entries entries
     * @param random random
     * @return rolled value
     */
    public static <T> T roll(@NotNull Collection<? extends WeightedEntry<T>> entries, @NotNull Random random) {
        double total = entries.stream()
                .mapToDouble(WeightedEntry::getChance)
                .sum();

        double roll = random.nextDouble() * total;
        double current = 0;

        for (WeightedEntry<T> entry : entries) {
            current += entry.getChance();
            if (roll < current)
                return entry.getValue();
        }

        return null;
    }
}
