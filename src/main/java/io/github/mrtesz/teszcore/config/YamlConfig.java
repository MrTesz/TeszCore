package io.github.mrtesz.teszcore.config;

import io.github.mrtesz.teszcore.copyable.Copyable;
import io.github.mrtesz.teszcore.exceptions.YamlConfigException;
import lombok.*;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.serializer.AnchorGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@SuppressWarnings("unused")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class YamlConfig implements Copyable<YamlConfig> {

    private final Path filePath;
    private final Yaml yaml;
    private final boolean autosave;

    @Setter
    private Map<String, Object> data;
    private Map<String, Object> defaults;

    private YamlConfig(@NotNull Path filePath, @NotNull Yaml yaml, boolean autosave) {
        this.filePath = filePath;
        this.yaml = yaml;
        this.autosave = autosave;

        load();
    }

    @SuppressWarnings("unchecked")
    public void load() throws YamlConfigException {
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
            throw new YamlConfigException("Could not load YAML file " + filePath, e);
        }
    }
    public void save() throws YamlConfigException {
        save(StandardCharsets.UTF_8);
    }
    public void save(@NotNull Charset cs) throws YamlConfigException {
        try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(filePath), cs)) {
            yaml.dump(data, writer);
        } catch (IOException e) {
            throw new YamlConfigException("Could not save YAML file " + filePath, e);
        }
    }

    public void addDefault(@NotNull String path, Object value) {
        defaults.put(path, value);
    }
    public void setDefault(@NotNull String path, Object value) {
        if (!contains(path))
            set(path, value);
    }

    @SuppressWarnings("unchecked")
    public void set(@NotNull String path, @Nullable Object value) {
        String[] keys = path.split("\\.");
        Map<String, Object> section = data;

        for (int i = 0; i < keys.length - 1; i++) {
            section = (Map<String, Object>) section.computeIfAbsent(
                    keys[i], k -> new LinkedHashMap<>()
            );
        }

        section.put(keys[keys.length - 1], value);
        if (autosave)
            save();
    }

    public String getString(@NotNull String path) {
        var val = get(path);
        return val != null ? String.valueOf(val) : null;
    }
    public String getString(@NotNull String path, String def) {
        var val = getString(path);
        return val != null ? val : def;
    }

    public int getInt(@NotNull String path) {
        return getInt(path, 0);
    }
    public int getInt(@NotNull String path, int def) {
        Object val = get(path);
        return val instanceof Number n ? n.intValue() : def;
    }
    public Integer getIntOrNull(@NotNull String path) {
        Object val = get(path);
        return val instanceof Number n ? n.intValue() : null;
    }

    public long getLong(@NotNull String path) {
        return this.getLong(path, 0);
    }
    public long getLong(@NotNull String path, long def) {
        Object val = get(path);
        return val instanceof Number n ? n.longValue() : def;
    }
    public Long getLongOrNull(@NotNull String path) {
        Object val = get(path);
        return val instanceof Number n ? n.longValue() : null;
    }

    public float getFloat(@NotNull String path) {
        return this.getFloat(path, 0);
    }
    public float getFloat(@NotNull String path, float def) {
        Object val = get(path);
        return val instanceof Number n ? n.floatValue() : def;
    }
    public Float getFloatOrNull(@NotNull String path) {
        Object val = get(path);
        return val instanceof Number n ? n.floatValue() : null;
    }

    public short getShort(@NotNull String path) {
        return this.getShort(path, (short) 0);
    }
    public short getShort(@NotNull String path, short def) {
        Object val = get(path);
        return val instanceof Number n ? n.shortValue() : def;
    }
    public Short getShortOrNull(@NotNull String path) {
        Object val = get(path);
        return val instanceof Number n ? n.shortValue() : null;
    }

    public double getDouble(@NotNull String path) {
        return getDouble(path, 0);
    }
    public double getDouble(@NotNull String path, double def) {
        Object val = get(path);
        return val instanceof Number n ? n.doubleValue() : def;
    }
    public Double getDoubleOrNull(@NotNull String path) {
        Object val = get(path);
        return val instanceof Number n ? n.doubleValue() : null;
    }

    public boolean getBoolean(@NotNull String path) {
        Object val = get(path);
        return val instanceof Boolean bool && bool;
    }
    public boolean getBoolean(@NotNull String path, boolean def) {
        Object val = get(path);
        return val instanceof Boolean b ? b : def;
    }
    public Boolean getBooleanOrNull(@NotNull String path) {
        Object val = get(path);
        return val instanceof Boolean b ? b : null;
    }

    public List<String> getStringList(@NotNull String path) {
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
    public List<String> getStringList(@NotNull String path, List<String> def) {
        Object val = get(path);
        if (val instanceof List<?>) {
            List<String> list = new ArrayList<>();
            for (Object o : (List<?>) val) {
                list.add(String.valueOf(o));
            }
            return list;
        }
        return def;
    }
    public List<String> getStringListOrNull(@NotNull String path) {
        Object val = get(path);
        if (val instanceof List<?>) {
            List<String> list = new ArrayList<>();
            for (Object o : (List<?>) val) {
                list.add(String.valueOf(o));
            }
            return list;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private @Nullable Object get(@NotNull String path) {
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

        return defaults.get(path);
    }

    public boolean contains(@NotNull String path) {
        return get(path) != null;
    }

    public boolean exists() {
        return Files.exists(filePath);
    }

    public Map<String, Object> getRaw() {
        return data;
    }

    @SuppressWarnings("unchecked")
    private Set<String> collectPaths(@NotNull Map<String, Object> section, @NotNull String parent) {
        Set<String> paths = new LinkedHashSet<>();

        for (Map.Entry<String, Object> entry : section.entrySet()) {
            String fullPath = parent.isEmpty() ? entry.getKey() : parent + "." + entry.getKey();

            if (entry.getValue() instanceof Map<?, ?> map) {
                paths.addAll(collectPaths((Map<String, Object>) map, fullPath));
            } else {
                paths.add(fullPath);
            }
        }

        return paths;
    }

    public Set<String> getAllPaths() {
        return collectPaths(data, "");
    }

    @SuppressWarnings("unchecked")
    public Set<String> getPaths(@NotNull String parent) {
        Object section = get(parent);
        if (!(section instanceof Map<?, ?> map))
            return Collections.emptySet();

        return collectPaths((Map<String, Object>) map, parent);
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor
    public static class Builder implements Copyable<Builder> {

        @Getter @Accessors(fluent = true)
        private @NotNull DumperOptions dumperOptions = new DumperOptions();

        /// Save the config after set() automatically. Default: true
        private boolean autosave = true;
        private @Nullable String filePath;
        private @NotNull String fileName;

        public YamlConfig build() {
            Objects.requireNonNull(fileName, "'fileName' in YamlConfig.Builder#build");
            Objects.requireNonNull(dumperOptions, "'dumperOptions' in YamlConfig.Builder#build");

            Path path = filePath == null
                    ? Paths.get(fileName + ".yml")
                    : Paths.get(filePath, fileName + ".yml");

            Yaml yaml = new Yaml(dumperOptions);

            return new YamlConfig(path, yaml, autosave);
        }

        public Builder setFilePath(@Nullable String filePath) {
            this.filePath = filePath;
            return this;
        }
        public Builder setFileName(@NotNull String fileName) {
            this.fileName = fileName;
            return this;
        }
        public Builder setAutosave(boolean autosave) {
            this.autosave = autosave;
            return this;
        }
        public Builder setDumperOptions(@NotNull DumperOptions dumperOptions) {
            this.dumperOptions = dumperOptions;
            return this;
        }

        public Builder setDefaultScalarStyle(@NotNull DumperOptions.ScalarStyle defaultStyle) {
            dumperOptions.setDefaultScalarStyle(defaultStyle);
            return this;
        }
        public Builder setDefaultFlowStyle(@NotNull DumperOptions.FlowStyle defaultFlowStyle) {
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
        public Builder setWidth(int bestWidth) {
            dumperOptions.setWidth(bestWidth);
            return this;
        }
        public Builder setSplitLines(boolean splitLines) {
            dumperOptions.setSplitLines(splitLines);
            return this;
        }
        public Builder setLineBreak(@NotNull DumperOptions.LineBreak lineBreak) {
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
        public Builder setTimeZone(@Nullable TimeZone timeZone) {
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
        public Builder setVersion(@Nullable DumperOptions.Version version) {
            dumperOptions.setVersion(version);
            return this;
        }
        public Builder setTags(@Nullable Map<String, String> tags) {
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

        @Override
        public Builder copy() {
            return new Builder(this.dumperOptions, this.autosave, this.filePath, this.fileName);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public YamlConfig copy() {
        return new YamlConfig(this.filePath, this.yaml, this.autosave, this.data, this.defaults);
    }
}
