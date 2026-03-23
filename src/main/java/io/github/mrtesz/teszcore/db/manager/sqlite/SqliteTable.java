package io.github.mrtesz.teszcore.db.manager.sqlite;

import io.github.mrtesz.teszcore.api.db.table.DBTable;
import io.github.mrtesz.teszcore.copyable.Copyable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SqliteTable implements DBTable {

    private final @Getter String name;
    private Map<String, String> columns = new LinkedHashMap<>();
    private @Nullable String unique = null;
    private Set<String> primaryKeyColumns = new HashSet<>();
    private Map<String, String> indexes = new HashMap<>();

    /**
     * Create a Table Object
     * @param name Name of the Table
     */
    public SqliteTable(String name) {
        this.name = name;
    }

    @Override
    public SqliteTable addColumn(String columnName, String definition) {
        columns.put(columnName, definition);
        return this;
    }

    @Override
    public SqliteTable setPrimaryKeys(String... keys) {
        primaryKeyColumns.clear();
        primaryKeyColumns.addAll(Arrays.stream(keys)
                .filter(columns::containsKey)
                .distinct()
                .toList());
        return this;
    }

    @Override
    public SqliteTable setUnique(String... unique) {
        this.unique = "UNIQUE(`" + String.join("`, `", unique) + "`)";
        return this;
    }

    @Override
    public SqliteTable addInt(String name) {
        return addColumn(name, "INTEGER");
    }
    @Override
    public SqliteTable addInt(String name, Integer def) {
        return addColumn(name, "INTEGER DEFAULT " + (def == null ? "NULL" : def));
    }

    @Override
    public SqliteTable addLong(String name) {
        return addBigInt(name);
    }
    @Override
    public SqliteTable addLong(String name, long def) {
        return addBigInt(name, def);
    }

    @Override
    public SqliteTable addBigInt(String name) {
        return addColumn(name, "INTEGER");
    }
    @Override
    public SqliteTable addBigInt(String name, long def) {
        return addColumn(name, "INTEGER DEFAULT " + def);
    }

    @Override
    public SqliteTable addDouble(String name) {
        return addColumn(name, "REAL");
    }
    @Override
    public SqliteTable addDouble(String name, double def) {
        return addColumn(name, "REAL DEFAULT " + def);
    }

    @Override
    public SqliteTable addFloat(String name) {
        return addColumn(name, "REAL");
    }
    @Override
    public SqliteTable addFloat(String name, float def) {
        return addColumn(name, "REAL DEFAULT " + def);
    }

    @Override
    public SqliteTable addText(String name) {
        return addColumn(name, "TEXT");
    }
    @Override
    public SqliteTable addText(String name, String def) {
        return addColumn(name, "TEXT DEFAULT " + def);
    }

    public SqliteTable addIndex(String indexName, String column) {
        indexes.put(indexName, column);
        return this;
    }

    public String getCreateCommand() {
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

        sb.append(")");
        return sb.toString();
    }

    public Map<String, String> getAlterColumnsCommands() {
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

    @Override
    public SqliteTable copy() {
        return new SqliteTable(this.name, this.columns, this.unique, this.primaryKeyColumns, this.indexes);
    }
}
