package de.mrtesz.dbutils.utils.logged;

import de.mrtesz.dbutils.api.DBUtils;
import de.mrtesz.dbutils.utils.logger.api.DebugLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Utility class for running {@link Runnable}s and {@link java.util.function.Supplier}s with wrapping logging
 */
@AllArgsConstructor
public class Runner {

    private final @Nullable String usingProject;

    /**
     * Referencing {@link #executeSupply(Supplier, String, String)} with usingProject being the String provided on construct
     */
    public <T> T get(@NotNull @NonNull Supplier<T> supplier, @Nullable String extraInfo) {
        return executeSupply(supplier, extraInfo, usingProject);
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
     * @return the return value of provided supplier
     */
    public static <T> T executeSupply(@NotNull @NonNull Supplier<T> supplier, @Nullable String extraInfo, @Nullable String usingProject) {
        DBUtils.getInstance().getLogger(DebugLevel.LEVEL5, usingProject).info("Supplying" + (extraInfo != null ? " " + extraInfo : "") + (usingProject != null ? " for project " + usingProject : "") + "...");
        long startTimestamp = System.currentTimeMillis();

        T returnValue = supplier.get();

        DBUtils.getInstance().getLogger(DebugLevel.LEVEL5, usingProject)
                .info("Supplied " + returnValue.getClass().getName() +
                        (extraInfo != null ? " " + extraInfo : "") +
                        (usingProject != null ? " for project " + usingProject : "") +
                        " in " + (System.currentTimeMillis() - startTimestamp) + "ms");
        return returnValue;
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
     */
    public static void executeRunnable(@NotNull @NonNull Runnable runnable, @Nullable String extraInfo, @Nullable String usingProject) {
        DBUtils.getInstance().getLogger(DebugLevel.LEVEL5, usingProject).info(
                "Running runnable" + 
                        (extraInfo != null ? " " + extraInfo : "") + 
                        (usingProject != null ? " for project " + usingProject : "") + 
                        "..."
        );
        long startTimestamp = System.currentTimeMillis();
        
        runnable.run();

        DBUtils.getInstance().getLogger(DebugLevel.LEVEL5, usingProject).info(
                "Ran runnable" + 
                        (extraInfo != null ? " " + extraInfo : "") + 
                        (usingProject != null ? " for project " + usingProject : "") + 
                        " in " + (System.currentTimeMillis() - startTimestamp) + "ms"
        );
    }
    public static void executeRunnable(@NotNull @NonNull Runnable runnable, @Nullable String extraInfo) {
        executeRunnable(runnable, extraInfo, null);
    }
    public static void executeRunnable(@NotNull @NonNull Runnable runnable) {
        executeRunnable(runnable, null, null);
    }
}
