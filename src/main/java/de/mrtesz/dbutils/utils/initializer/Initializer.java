package de.mrtesz.dbutils.utils.initializer;

import de.mrtesz.dbutils.api.DBUtils;
import de.mrtesz.dbutils.utils.logger.api.DebugLevel;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.function.Supplier;

@AllArgsConstructor
public class Initializer {

    private final String usingProject;

    /**
     * Referencing {@link #initialize(Class, String, Object...)} with the String being the  provided when construction
     */
    public <I> I init(Class<I> clazz, Object... parameters) {
        return initialize(clazz, usingProject, parameters);
    }
    public <C> C init(Class<C> clazz, Supplier<C> supplier) {
        return initialize(clazz, supplier, usingProject);
    }

    // statics
    /**
     * Initialize a class with wrapping logging (Example: <code>de.mrtesz.dbutils.utils.config.YamlConfig</code>: <code>initialize(YamlConfig.class, "config")</code>)
     * @param clazz The class of the object, initializing
     * @param parameters The parameters of the constructor you want to use
     * @return An instance of the clazz param
     * @param <I> The type of the instance, initialized
     */
    public static <I> I initialize(Class<I> clazz, Object... parameters) {
        return initialize(clazz, null, parameters);
    }
    /**
     * Initialize a class with wrapping logging (Example: <code>de.mrtesz.dbutils.utils.config.YamlConfig</code>: <code>initialize(YamlConfig.class, "config")</code>)
     * @param clazz The class of the object, initializing
     * @param usingProject Optional: The project using the method
     * @param parameters The parameters of the constructor you want to use
     * @return An instance of the clazz param
     * @param <I> The type of the instance, initialized
     */
    public static <I> I initialize(Class<I> clazz, @Nullable String usingProject, Object... parameters) {
        DBUtils.getInstance().getLogger(DebugLevel.LEVEL5, usingProject)
                .info("Initializing " + clazz.getName() + (usingProject != null ? " for project " + usingProject : "") + "...");

        try {
            Constructor<I> constructor = findMatchingConstructor(clazz, parameters);
            constructor.setAccessible(true);
            I instance = constructor.newInstance(parameters);

            DBUtils.getInstance().getLogger(DebugLevel.LEVEL5, usingProject)
                    .info("Initialized " + clazz.getName() + (usingProject != null ? " for project " + usingProject : ""));
            return instance;
        } catch (Exception e) {
            DBUtils.getInstance().getLogger(DebugLevel.LEVEL1, usingProject).error("Failed to initialize " + clazz.getName() + (usingProject != null ? " for project " + usingProject : ""));
            throw new RuntimeException(e);
        }
    }

    public static <C> C initialize(Class<C> clazz, Supplier<C> supplier, @Nullable String usingProject) {
        DBUtils.getInstance().getLogger(DebugLevel.LEVEL5, usingProject)
                .info("Initializing " + clazz.getName() + " with Supplier<" + clazz.getSimpleName() + ">" + (usingProject != null ? " for project " + usingProject : "") + "...");

        try {
            C returnValue = supplier.get();

            DBUtils.getInstance().getLogger(DebugLevel.LEVEL5, usingProject)
                    .info("Initialized " + clazz.getName() + " with Supplier<" + clazz.getSimpleName() + ">" + (usingProject != null ? " for project " + usingProject : ""));

            return returnValue;
        } catch (Exception e) {
            DBUtils.getInstance().getLogger(DebugLevel.LEVEL1, usingProject)
                    .error("Failed to initialize " + clazz.getName() + " with Supplier<" + clazz.getSimpleName() + ">" + (usingProject != null ? " for project " + usingProject : ""));
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <I> Constructor<I> findMatchingConstructor(Class<I> clazz, Object[] parameters) throws NoSuchMethodException {
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
            return (Constructor<I>) constructor;
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
}
