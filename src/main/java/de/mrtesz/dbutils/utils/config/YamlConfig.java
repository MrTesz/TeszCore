package de.mrtesz.dbutils.utils.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.serializer.AnchorGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@SuppressWarnings("unused")
public class YamlConfig {

    private final Path filePath;
    private final Yaml yaml;
    private Map<String, Object> data;

    private YamlConfig(@NotNull Path filePath, @NotNull Yaml yaml) {
        this.filePath = filePath;
        this.yaml = yaml;

        load();
    }

    public static class Builder {

        @Getter @Accessors(fluent = true)
        private @NotNull DumperOptions dumperOptions = new DumperOptions();

        private @Nullable String filePath;

        private String fileName;

        public YamlConfig build() {
            Objects.requireNonNull(fileName);

            Path path = filePath == null
                    ? Paths.get(fileName + ".yml")
                    : Paths.get(filePath, fileName + ".yml");

            Yaml yaml = new Yaml(dumperOptions);

            return new YamlConfig(path, yaml);
        }

        public Builder setFilePath(@Nullable String filePath) {
            this.filePath = filePath;
            return this;
        }
        public Builder setFileName(@NotNull String fileName) {
            this.fileName = fileName;
            return this;
        }
        public Builder setDumperOptions(DumperOptions dumperOptions) {
            this.dumperOptions = dumperOptions;
            return this;
        }

        public Builder setDefaultScalarStyle(DumperOptions.ScalarStyle defaultStyle) {
            dumperOptions.setDefaultScalarStyle(defaultStyle);
            return this;
        }
        public Builder setDefaultFlowStyle(DumperOptions.FlowStyle defaultFlowStyle) {
            dumperOptions.setDefaultFlowStyle(defaultFlowStyle);
            return this;
        }
        public Builder setCanonical(boolean canonical) {
            dumperOptions.setCanonical(canonical);
            return this;
        }
        public Builder setAllowUnicode(boolean allowUnicode) {
            dumperOptions.setAllowUnicode(allowUnicode);
            return this;
        }
        public Builder setAllowReadOnlyProperties(boolean allowReadOnlyProperties) {
            dumperOptions.setAllowReadOnlyProperties(allowReadOnlyProperties);
            return this;
        }
        public Builder setIndent(int indent) {
            dumperOptions.setIndent(indent);
            return this;
        }
        public Builder setIndicatorIndent(int indicatorIndent) {
            dumperOptions.setIndicatorIndent(indicatorIndent);
            return this;
        }
        public Builder setIndentWithIndicator(boolean indentWithIndicator) {
            dumperOptions.setIndentWithIndicator(indentWithIndicator);
            return this;
        }
        public Builder setWith(int bestWidth) {
            dumperOptions.setWidth(bestWidth);
            return this;
        }
        public Builder setSplitLines(boolean splitLines) {
            dumperOptions.setSplitLines(splitLines);
            return this;
        }
        public Builder setLineBreak(DumperOptions.LineBreak lineBreak) {
            dumperOptions.setLineBreak(lineBreak);
            return this;
        }
        public Builder setExplicitStart(boolean explicitStart) {
            dumperOptions.setExplicitStart(explicitStart);
            return this;
        }
        public Builder setExplicitEnd(boolean explicitEnd) {
            dumperOptions.setExplicitEnd(explicitEnd);
            return this;
        }
        public Builder setTimeZone(TimeZone timeZone) {
            dumperOptions.setTimeZone(timeZone);
            return this;
        }
        public Builder setMaxSimpleKeyLength(int maxSimpleKeyLength) {
            dumperOptions.setMaxSimpleKeyLength(maxSimpleKeyLength);
            return this;
        }
        public Builder setProcessComments(boolean processComments) {
            dumperOptions.setProcessComments(processComments);
            return this;
        }
        public Builder setNonPrintableStyle(DumperOptions.NonPrintableStyle nonPrintableStyle) {
            dumperOptions.setNonPrintableStyle(nonPrintableStyle);
            return this;
        }
        public Builder setVersion(DumperOptions.Version version) {
            dumperOptions.setVersion(version);
            return this;
        }
        public Builder setTags(Map<String, String> tags) {
            dumperOptions.setTags(tags);
            return this;
        }
        public Builder setPrettyFlow(Boolean prettyFlow) {
            dumperOptions.setPrettyFlow(prettyFlow);
            return this;
        }
        public Builder setAnchorGenerator(AnchorGenerator anchorGenerator) {
            dumperOptions.setAnchorGenerator(anchorGenerator);
            return this;
        }
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
        return getInt(path, 0);
    }
    public int getInt(String path, int def) {
        Object val = get(path);
        return val instanceof Number n ? n.intValue() : def;
    }
    public Integer getIntOrElse(String path, Integer def) {
        Object val = get(path);
        return val instanceof Number n ? n.intValue() : def;
    }

    public long getLong(String path) {
        return this.getLong(path, 0);
    }
    public long getLong(String path, long def) {
        Object val = get(path);
        return val instanceof Number n ? n.longValue() : def;
    }
    public Long getLongOrElse(String path, Long def) {
        Object val = get(path);
        return val instanceof Number n ? n.longValue() : def;
    }

    public float getFloat(String path) {
        return this.getFloat(path, 0);
    }
    public float getFloat(String path, float def) {
        Object val = get(path);
        return val instanceof Number n ? n.floatValue() : def;
    }
    public Float getFloatOrElse(String path, Float def) {
        Object val = get(path);
        return val instanceof Number n ? n.floatValue() : def;
    }

    public short getShort(String path) {
        return this.getShort(path, (short) 0);
    }
    public short getShort(String path, short def) {
        Object val = get(path);
        return val instanceof Number n ? n.shortValue() : def;
    }
    public Short getShortOrElse(String path, Short def) {
        Object val = get(path);
        return val instanceof Number n ? n.shortValue() : def;
    }

    public double getDouble(String path) {
        return getDouble(path, 0);
    }
    public double getDouble(String path, double def) {
        Object val = get(path);
        return val instanceof Number n ? n.doubleValue() : def;
    }
    public Double getDoubleOrElse(String path, Double def) {
        Object val = get(path);
        return val instanceof Number n ? n.doubleValue() : def;
    }

    public boolean getBoolean(String path) {
        Object val = get(path);
        return val instanceof Boolean && (Boolean) val;
    }
    public boolean getBoolean(String path, boolean def) {
        Object val = get(path);
        return val instanceof Boolean b ? b : def;
    }
    public Boolean getBooleanOrElse(String path, Boolean def) {
        Object val = get(path);
        return val instanceof Boolean b ? b : def;
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

    public static Builder builder() {
        return new Builder();
    }
}
