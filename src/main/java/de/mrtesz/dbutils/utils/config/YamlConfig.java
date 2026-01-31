package de.mrtesz.dbutils.utils.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class YamlConfig {

    private final Path filePath;
    private final Yaml yaml;
    private Map<String, Object> data;

    public YamlConfig(@NotNull String name) {
        this(null, name);
    }
    public YamlConfig(@Nullable String path, @NotNull String name) {
        this.filePath = path == null
                ? Paths.get(name + ".yml")
                : Paths.get(path, name + ".yml");

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setIndent(2);
        options.setPrettyFlow(true);

        yaml = new Yaml(options);
        load();
    }

    @SuppressWarnings("unchecked")
    public void load() {
        try {
            if (filePath.getParent() != null)
                Files.createDirectories(filePath.getParent());

            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
                data = new LinkedHashMap<>();
                save();
                return;
            }

            try (InputStream in = Files.newInputStream(filePath)) {
                Object loaded = yaml.load(in);
                data = loaded instanceof Map ? (Map<String, Object>) loaded : new LinkedHashMap<>();
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not load YAML file " + filePath, e);
        }
    }
    public void save() {
        try (OutputStreamWriter writer =
                     new OutputStreamWriter(Files.newOutputStream(filePath), StandardCharsets.UTF_8)) {
            yaml.dump(data, writer);
        } catch (IOException e) {
            throw new RuntimeException("Could not save YAML file " + filePath, e);
        }
    }

    public void addDefault(String path, Object value) {
        if (!contains(path)) {
            set(path, value);
        }
    }

    @SuppressWarnings("unchecked")
    public void set(String path, Object value) {
        String[] keys = path.split("\\.");
        Map<String, Object> section = data;

        for (int i = 0; i < keys.length - 1; i++) {
            section = (Map<String, Object>) section.computeIfAbsent(
                    keys[i], k -> new LinkedHashMap<>()
            );
        }

        section.put(keys[keys.length - 1], value);
        save();
    }

    public String getString(String path) {
        Object val = get(path);
        return val != null ? String.valueOf(val) : null;
    }
    public String getString(String path, String def) {
        String val = getString(path);
        return val != null ? val : def;
    }

    public int getInt(String path) {
        Object val = get(path);
        return val instanceof Number ? ((Number) val).intValue() : 0;
    }
    public int getInt(String path, int def) {
        Object val = get(path);
        return val instanceof Number ? ((Number) val).intValue() : def;
    }

    public long getLong(String path) {
        return this.getLong(path, 0);
    }
    public long getLong(String path, long def) {
        Object val = get(path);
        return val instanceof Number ? ((Number) val).longValue() : def;
    }

    public float getFloat(String path) {
        return this.getFloat(path, 0);
    }
    public float getFloat(String path, float def) {
        Object val = get(path);
        return val instanceof Number ? ((Number) val).floatValue() : def;
    }

    public short getShort(String path) {
        return this.getShort(path, (short) 0);
    }
    public short getShort(String path, short def) {
        Object val = get(path);
        return val instanceof Number ? ((Number) val).shortValue() : def;
    }

    public double getDouble(String path) {
        return getDouble(path, 0);
    }
    public double getDouble(String path, double def) {
        Object val = get(path);
        return val instanceof Number ? ((Number) val).doubleValue() : def;
    }

    public boolean getBoolean(String path) {
        Object val = get(path);
        return val instanceof Boolean && (Boolean) val;
    }
    public boolean getBoolean(String path, boolean def) {
        Object val = get(path);
        return val instanceof Boolean ? (Boolean) val : def;
    }

    public List<String> getStringList(String path) {
        Object val = get(path);
        if (val instanceof List<?>) {
            List<String> list = new ArrayList<>();
            for (Object o : (List<?>) val) {
                list.add(String.valueOf(o));
            }
            return list;
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    private Object get(String path) {
        String[] keys = path.split("\\.");
        Map<String, Object> section = data;

        for (int i = 0; i < keys.length; i++) {
            Object val = section.get(keys[i]);

            if (val == null)
                return null;

            if (i == keys.length - 1)
                return val;

            if (!(val instanceof Map))
                return null;

            section = (Map<String, Object>) val;
        }

        return null;
    }

    public boolean contains(String path) {
        return get(path) != null;
    }

    public boolean exists() {
        return Files.exists(filePath);
    }

    public Map<String, Object> getRaw() {
        return data;
    }
}
