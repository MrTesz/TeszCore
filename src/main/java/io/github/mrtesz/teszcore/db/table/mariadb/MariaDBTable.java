package io.github.mrtesz.teszcore.db.table.mariadb;

import io.github.mrtesz.teszcore.api.db.table.DBTable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MariaDBTable implements DBTable {

    @Getter
    private final String name;
    private Map<String, String> columns = new LinkedHashMap<>();
    private List<String> primaryKeyColumns = new ArrayList<>();
    private Map<String, String> indexes = new HashMap<>();
    private List<String> uniques = new ArrayList<>();

    /**
     * Create a {@link MariaDBTable} Object
     * @param name Name of the Table
     */
    public MariaDBTable(@NotNull String name) {
        this.name = name;
    }

    @Override
    public MariaDBTable addColumn(@NotNull String columnName, @NotNull String definition) {
        columns.put(columnName, definition);
        return this;
    }

    @Override
    public MariaDBTable setPrimaryKeys(String... keys) {
        primaryKeyColumns.clear();
        primaryKeyColumns.addAll(Arrays.stream(keys)
                .filter(columns::containsKey)
                .distinct()
                .toList());
        return this;
    }

    @Override
    public MariaDBTable addUnique(String... unique) {
        this.uniques.add("UNIQUE(`" + String.join("`, `", unique) + "`)");
        return this;
    }

    @Override
    public MariaDBTable addInt(@NotNull String name) {
        return addColumn(name, "INT");
    }
    @Override
    public MariaDBTable addInt(@NotNull String name, Integer def) {
        return addColumn(name, "INT DEFAULT " + (def == null ? "NULL" : def));
    }

    @Override
    public MariaDBTable addLong(@NotNull String name) {
        return addBigInt(name);
    }
    @Override
    public MariaDBTable addLong(@NotNull String name, long def) {
        return addBigInt(name, def);
    }

    @Override
    public MariaDBTable addBigInt(@NotNull String name) {
        return addColumn(name, "BIGINT");
    }
    @Override
    public MariaDBTable addBigInt(@NotNull String name, long def) {
        return addColumn(name, "BIGINT DEFAULT " + def);
    }

    @Override
    public MariaDBTable addDouble(@NotNull String name) {
        return addColumn(name, "DOUBLE");
    }
    @Override
    public MariaDBTable addDouble(@NotNull String name, double def) {
        return addColumn(name, "DOUBLE DEFAULT " + def);
    }

    @Override
    public MariaDBTable addFloat(@NotNull String name) {
        return addColumn(name, "FLOAT");
    }
    @Override
    public MariaDBTable addFloat(@NotNull String name, float def) {
        return addColumn(name, "FLOAT DEFAULT " + def);
    }

    @Override
    public MariaDBTable addText(@NotNull String name) {
        return addColumn(name, "TEXT");
    }
    @Override
    public MariaDBTable addText(@NotNull String name, String def) {
        return addColumn(name, "TEXT DEFAULT " + def);
    }

    public MariaDBTable addVarchar(@NotNull String name, int length) {
        return addColumn(name, "VARCHAR(" + length + ")");
    }
    public MariaDBTable addVarchar(@NotNull String name, int length, String def) {
        return addColumn(name, "VARCHAR(" + length + ") DEFAULT " + def);
    }

    public MariaDBTable addMediumText(@NotNull String name) {
        return addColumn(name, "MEDIUMTEXT");
    }
    public MariaDBTable addMediumText(@NotNull String name, String def) {
        return addColumn(name, "MEDIUMTEXT DEFAULT " + def);
    }

    public MariaDBTable addLongText(@NotNull String name) {
        return addColumn(name, "LONGTEXT");
    }
    public MariaDBTable addLongText(@NotNull String name, String def) {
        return addColumn(name, "LONGTEXT DEFAULT " + def);
    }

    public MariaDBTable addBoolean(@NotNull String name) {
        return addColumn(name, "BOOLEAN");
    }
    public MariaDBTable addBoolean(@NotNull String name, boolean def) {
        return addColumn(name, "BOOLEAN DEFAULT " + def);
    }

    public MariaDBTable addDate(@NotNull String name) {
        return addColumn(name, "DATE");
    }
    public MariaDBTable addDateNow(@NotNull String name) {
        return addColumn(name, "DATE DEFAULT (CURRENT_DATE)");
    }

    public MariaDBTable addTimestamp(@NotNull String name) {
        return addColumn(name, "TIMESTAMP");
    }
    public MariaDBTable addTimestampNow(@NotNull String name) {
        return addColumn(name, "TIMESTAMP DEFAULT CURRENT_TIMESTAMP");
    }

    @Override
    public MariaDBTable addIndex(String indexName, String column) {
        indexes.put(indexName, "INDEX " + indexName + " (`" + column + "`)");
        return this;
    }

    @Override
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
        if (!uniques.isEmpty()) {
            for (String unique : uniques) {
                sb.append(", ").append(unique);
            }
        }

        for (String index : indexes.values()) {
            sb.append(", ").append(index);
        }

        sb.append(")");
        return sb.toString();
    }

    @Override
    public Map<String, String> getAlterColumnsCommands() {
        Map<String, String> commands = new HashMap<>();
        for (Map.Entry<String, String> entry : columns.entrySet()) {
            commands.put(entry.getKey(), "ALTER TABLE `" + name + "` ADD COLUMN `" +
                    entry.getKey() + "` " + entry.getValue());
        }

        return commands;
    }
    @Override
    public Map<String, String> getAlterIndexCommands() {
        Map<String, String> commands = new HashMap<>();
        for (Map.Entry<String, String> entry : indexes.entrySet()) {
            commands.put(entry.getKey(), "ALTER TABLE `" + name + "` ADD " + entry.getValue());
        }

        return commands;
    }

    @Override
    public MariaDBTable copy() {
        return new MariaDBTable(name, columns, primaryKeyColumns, indexes, uniques);
    }
}
