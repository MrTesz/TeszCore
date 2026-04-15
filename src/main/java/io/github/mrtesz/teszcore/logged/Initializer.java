package io.github.mrtesz.teszcore.logged;

import io.github.mrtesz.teszcore.api.TeszCoreApi;
import io.github.mrtesz.teszcore.copyable.Copyable;
import io.github.mrtesz.teszcore.exceptions.InitializerException;
import io.github.mrtesz.teszcore.logger.TeszCoreLogger;
import io.github.mrtesz.teszcore.logger.level.DebugLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Utility class for initializing classes with wrapping logging
 */
@AllArgsConstructor
public class Initializer implements Copyable<Initializer> {

    private final @Nullable String usingProject;
    private final @NotNull TeszCoreLogger logger;

    public Initializer(@Nullable String usingProject) {
        this(usingProject, TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL5, usingProject));
    }

    /**
     * {@link #initialize(Class, String, TeszCoreLogger, Object...)}, with {@link #usingProject} and {@link #logger}
     * @see #initialize(Class, String, TeszCoreLogger, Object...)
     */
    public <T> T init(@NotNull Class<T> clazz, Object... parameters) throws InitializerException {
        return initialize(clazz, usingProject, logger, parameters);
    }

    // statics
    /**
     * Initialize a class with wrapping logging (Example for {@link Runner}: <code>Initializer.initialize({@link Runner}.class, "TeszCore")</code>)
     * @param clazz Class of the object, initializing
     * @param usingProject Optional: Project using the method
     * @param logger Logger
     * @param parameters Parameters of the constructor you want to use
     * @return An instance of the clazz param
     * @param <T> The type of the instance, initialized
     */
    public static <T> T initialize(@NotNull Class<T> clazz, @Nullable String usingProject, @NotNull TeszCoreLogger logger, Object... parameters) throws InitializerException {
        logger.info("Initializing " + clazz.getName() + (usingProject != null ? " for project " + usingProject : "") + "...");

        try {
            Constructor<T> constructor = findMatchingConstructor(clazz, parameters);
            constructor.setAccessible(true);
            T instance = constructor.newInstance(parameters);

            logger.info("Initialized " + clazz.getName() + (usingProject != null ? " for project " + usingProject : ""));
            return instance;
        } catch (Exception e) {
            String errorMessage = "Failed to initialize " + clazz.getName() + (usingProject != null ? " for project " + usingProject : "");
            getErrorLogger(logger).error(errorMessage);
            throw new InitializerException(errorMessage, e);
        }
    }

    /** {@link #initialize(Class, String, TeszCoreLogger, Object...)}, using a LEVEL5 logger
     * @see #initialize(Class, String, TeszCoreLogger, Object...) */
    public static <T> T initialize(@NotNull Class<T> clazz, @Nullable String usingProject, Object... parameters) throws InitializerException {
        return initialize(clazz, usingProject, TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL5, usingProject), parameters);
    }
    /** {@link #initialize(Class, String, TeszCoreLogger, Object...)}, with {@code usingProject} = null
     * @see #initialize(Class, String, TeszCoreLogger, Object...) */
    public static <T> T initialize(@NotNull Class<T> clazz, @NotNull TeszCoreLogger logger, Object... parameters) throws InitializerException {
        return initialize(clazz, null, logger, parameters);
    }
    /** {@link #initialize(Class, String, TeszCoreLogger, Object...)}, with {@code usingProject} = null and a LEVEL5 logger
     * @see #initialize(Class, String, TeszCoreLogger, Object...) */
    public static <T> T initialize(@NotNull Class<T> clazz, Object... parameters) throws InitializerException {
        return initialize(clazz, null, TeszCoreApi.getInstance().getLogger(DebugLevel.LEVEL5, null), parameters);
    }
    
    @SuppressWarnings("unchecked")
    private static <T> Constructor<T> findMatchingConstructor(@NotNull Class<T> clazz, Object[] parameters) throws NoSuchMethodException {
        outer:
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            Class<?>[] paramTypes = constructor.getParameterTypes();
            if (paramTypes.length != parameters.length) continue;

            for (int i = 0; i < paramTypes.length; i++) {
                Class<?> paramType = paramTypes[i];
                Object arg = parameters[i];
                if (arg == null) continue;

                if (paramType.isPrimitive()) {
                    if (!wrapperToPrimitive(arg.getClass()).equals(paramType)) {
                        continue outer;
                    }
                } else if (!paramType.isAssignableFrom(arg.getClass())) {
                    continue outer;
                }
            }
            return (Constructor<T>) constructor;
        }
        throw new NoSuchMethodException("No matching constructor found for " + clazz.getSimpleName());
    }
    private static Class<?> wrapperToPrimitive(Class<?> wrapper) {
        if (wrapper == Boolean.class) return boolean.class;
        if (wrapper == Byte.class) return byte.class;
        if (wrapper == Character.class) return char.class;
        if (wrapper == Short.class) return short.class;
        if (wrapper == Integer.class) return int.class;
        if (wrapper == Long.class) return long.class;
        if (wrapper == Float.class) return float.class;
        if (wrapper == Double.class) return double.class;
        return wrapper;
    }

    private static TeszCoreLogger getErrorLogger(TeszCoreLogger logger) {
        TeszCoreLogger errorLogger = logger.copy();
        errorLogger.setLevel(DebugLevel.LEVEL1);
        return errorLogger;
    }

    @Override
    public Initializer copy() {
        return new Initializer(this.usingProject, this.logger);
    }
}
