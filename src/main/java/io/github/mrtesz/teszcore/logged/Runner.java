package io.github.mrtesz.teszcore.logged;

import io.github.mrtesz.teszcore.api.TeszCoreApi;
import io.github.mrtesz.teszcore.copyable.Copyable;
import io.github.mrtesz.teszcore.logger.TeszCoreLogger;
import io.github.mrtesz.teszcore.logger.level.DebugLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Utility class for running {@link Runnable}s and {@link java.util.function.Supplier}s with wrapping logging
 */
@AllArgsConstructor
public class Runner implements Copyable<Runner> {

    private final @Nullable String usingProject;
    private final @NonNull @NotNull TeszCoreLogger logger;

    public Runner(String usingProject) {
        this(usingProject, TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL5, usingProject));
    }

    /**
     * Referencing {@link #executeSupply(Supplier, String, String, TeszCoreLogger)} with usingProject and logger being the params provided on construct
     */
    public <T> T get(@NotNull @NonNull Supplier<T> supplier, @Nullable String extraInfo) {
        return executeSupply(supplier, extraInfo, usingProject, logger);
    }
    public <T> T get(@NotNull @NonNull Supplier<T> supplier) {
        return get(supplier, null);
    }

    /**
     * Referencing {@link #executeRunnable(Runnable, String, String)} with usingProject being the String provided on construct
     */
    public void run(@NotNull @NonNull Runnable runnable, @Nullable String extraInfo) {
        executeRunnable(runnable, extraInfo, usingProject);
    }
    public void run(@NotNull @NonNull Runnable runnable) {
        run(runnable, null);
    }

    /**
     * Execute a {@link Supplier} with wrapping logging
     * @param supplier Supplier to execute
     * @param extraInfo Optional: Extra infos to display in the logging
     * @param usingProject Optional: Project using the method
     * @param logger Logger
     * @return the return value of provided supplier
     */
    public static <T> T executeSupply(@NotNull @NonNull Supplier<T> supplier, @Nullable String extraInfo, @Nullable String usingProject, @NotNull @NonNull TeszCoreLogger logger) {
        logger.info("Supplying" + (extraInfo != null ? " " + extraInfo : "") + (usingProject != null ? " for project " + usingProject : "") + "...");
        long startTimestamp = System.currentTimeMillis();

        T returnValue = supplier.get();

        logger.info("Supplied " + returnValue.getClass().getName() +
                        (extraInfo != null ? " " + extraInfo : "") +
                        (usingProject != null ? " for project " + usingProject : "") +
                        " in " + (System.currentTimeMillis() - startTimestamp) + "ms");
        return returnValue;
    }
    /**
     * Execute a {@link Supplier} with wrapping logging
     * @param supplier Supplier to execute
     * @param extraInfo Optional: Extra infos to display in the logging
     * @param usingProject Optional: Project using the method
     * @return the return value of provided supplier
     */
    public static <T> T executeSupply(@NotNull @NonNull Supplier<T> supplier, @Nullable String extraInfo, @Nullable String usingProject) {
        return executeSupply(supplier, extraInfo, usingProject, TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL5, usingProject));
    }
    public static <T> T executeSupply(@NotNull @NonNull Supplier<T> supplier, @Nullable String extraInfo) {
        return executeSupply(supplier, extraInfo, null);
    }
    public static <T> T executeSupply(@NotNull @NonNull Supplier<T> supplier) {
        return executeSupply(supplier, null, null);
    }

    /**
     * Execute a {@link Runnable} with wrapping logging
     * @param runnable Runnable to execute
     * @param extraInfo Optional: Extra infos to display in the logging
     * @param usingProject Optional: Project using the method
     * @param logger Logger
     */
    public static void executeRunnable(@NotNull @NonNull Runnable runnable, @Nullable String extraInfo, @Nullable String usingProject, @NotNull @NonNull TeszCoreLogger logger) {
        logger.info("Running runnable" +
                        (extraInfo != null ? " " + extraInfo : "") +
                        (usingProject != null ? " for project " + usingProject : "") +
                        "..."
        );
        long startTimestamp = System.currentTimeMillis();

        runnable.run();

        logger.info("Ran runnable" +
                        (extraInfo != null ? " " + extraInfo : "") +
                        (usingProject != null ? " for project " + usingProject : "") +
                        " in " + (System.currentTimeMillis() - startTimestamp) + "ms"
        );
    }
    /**
     * Execute a {@link Runnable} with wrapping logging
     * @param runnable Runnable to execute
     * @param extraInfo Optional: Extra infos to display in the logging
     * @param usingProject Optional: Project using the method
     */
    public static void executeRunnable(@NotNull @NonNull Runnable runnable, @Nullable String extraInfo, @Nullable String usingProject) {
        executeRunnable(runnable, extraInfo, usingProject, TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL5, usingProject));
    }
    public static void executeRunnable(@NotNull @NonNull Runnable runnable, @Nullable String extraInfo) {
        executeRunnable(runnable, extraInfo, null);
    }
    public static void executeRunnable(@NotNull @NonNull Runnable runnable) {
        executeRunnable(runnable, null, null);
    }

    @Override
    public Runner copy() {
        return new Runner(this.usingProject, this.logger);
    }
}
