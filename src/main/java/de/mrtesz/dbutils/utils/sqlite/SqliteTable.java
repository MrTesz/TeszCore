package de.mrtesz.dbutils.utils.sqlite;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class SqliteTable {

    @Getter
    private final String name;
    private final Map<String, String> columns = new LinkedHashMap<>();
    private final List<String> primaryKeyColumns = new ArrayList<>();
    private @Nullable String unique = null;
    private final Map<String, String> indexes = new HashMap<>();

    /**
     * Create a Table Object
     * @param name Name of the Table
     */
    public SqliteTable(String name) {
        this.name = name;
    }

    private SqliteTable addColumn(String columnName, String definition) {
        columns.put(columnName, definition);
        return this;
    }

    /**
     * Set the Primary Keys of the Table
     * @param keys The keys, wich should be turned into Primarys
     */
    public SqliteTable setPrimaryKeys(String... keys) {
        primaryKeyColumns.clear();
        primaryKeyColumns.addAll(Arrays.stream(keys)
                .filter(columns::containsKey)
                .distinct()
                .toList());
        return this;
    }

    public SqliteTable setUnique(String... unique) {
        this.unique = "UNIQUE(`" + String.join("`, `", unique) + "`)";
        return this;
    }

    public SqliteTable addInt(String name) {
        return addColumn(name, "INTEGER");
    }
    public SqliteTable addInt(String name, Integer def) {
        return addColumn(name, "INTEGER DEFAULT " + (def == null ? "NULL" : def));
    }

    public SqliteTable addLong(String name) {
        return addBigInt(name);
    }
    public SqliteTable addLong(String name, long def) {
        return addBigInt(name, def);
    }

    public SqliteTable addBigInt(String name) {
        return addColumn(name, "INTEGER");
    }
    public SqliteTable addBigInt(String name, long def) {
        return addColumn(name, "INTEGER DEFAULT " + def);
    }

    public SqliteTable addDouble(String name) {
        return addColumn(name, "REAL");
    }
    public SqliteTable addDouble(String name, double def) {
        return addColumn(name, "REAL DEFAULT " + def);
    }

    public SqliteTable addFloat(String name) {
        return addColumn(name, "REAL");
    }
    public SqliteTable addFloat(String name, float def) {
        return addColumn(name, "REAL DEFAULT " + def);
    }

    public SqliteTable addBoolean(String name) {
        return addColumn(name, "INTEGER");
    }
    public SqliteTable addBoolean(String name, boolean def) {
        return addColumn(name, "INTEGER DEFAULT " + (def ? 1 : 0));
    }

    public SqliteTable addText(String name) {
        return addColumn(name, "TEXT");
    }
    public SqliteTable addText(String name, String def) {
        return addColumn(name, "TEXT DEFAULT " + def);
    }

    public SqliteTable addIndex(String indexName, String column) {
        indexes.put(indexName, column);
        return this;
    }

    protected String getCreateCommand() {
        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS `").append(name).append("` (");
        boolean first = true;
        for (Map.Entry<String, String> entry : columns.entrySet()) {
            if (!first) sb.append(", ");
            sb.append("`").append(entry.getKey()).append("` ").append(entry.getValue());
            first = false;
        }
        if (!primaryKeyColumns.isEmpty()) {
            sb.append(", PRIMARY KEY (");
            sb.append(primaryKeyColumns.stream()
                    .map(k -> "`" + k + "`")
                    .collect(Collectors.joining(", ")));
            sb.append(")");
        }
        if (unique != null)
            sb.append(", ").append(unique);

        for (String index : indexes.values()) {
            sb.append(", ").append(index);
        }

        sb.append(")");
        return sb.toString();
    }

    protected Map<String, String> getAlterColumnsCommands() {
        Map<String, String> commands = new HashMap<>();
        for (Map.Entry<String, String> entry : columns.entrySet()) {
            commands.put(entry.getKey(), "ALTER TABLE `" + name + "` ADD COLUMN `" +
                    entry.getKey() + "` " + entry.getValue());
        }

        return commands;
    }

    public Map<String, String> getAlterIndexCommands() {
        Map<String, String> commands = new HashMap<>();
        for (Map.Entry<String, String> entry : indexes.entrySet()) {
            String indexName = entry.getKey();
            String column = entry.getValue();
            commands.put(indexName,
                    "CREATE INDEX IF NOT EXISTS `" + indexName + "` ON `" + name + "`(`" + column + "`)");
        }
        return commands;
    }

}
