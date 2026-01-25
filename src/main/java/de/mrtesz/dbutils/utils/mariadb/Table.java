package de.mrtesz.dbutils.utils.mariadb;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class Table {

    @Getter
    private final String name;
    private final Map<String, String> columns = new LinkedHashMap<>();
    private final List<String> primaryKeyColumns = new ArrayList<>();
    private final Map<String, String> indexes = new HashMap<>();
    private @Nullable String unique = null;

    /**
     * Create a Table Object
     * @param name Name of the Table
     */
    public Table(String name) {
        this.name = name;
    }

    private Table addColumn(String columnName, String definition) {
        columns.put(columnName, definition);
        return this;
    }

    /**
     * Set the Primary Keys of the Table like an uuid in playerInfos or itemId in auctionItems
     * @param keys The keys, wich should be turned into Primarys
     */
    public Table setPrimaryKeys(String... keys) {
        primaryKeyColumns.clear();
        primaryKeyColumns.addAll(Arrays.stream(keys)
                .filter(columns::containsKey)
                .distinct()
                .toList());
        return this;
    }

    public Table setUnique(String... unique) {
        this.unique = "UNIQUE(`" + String.join("`, `", unique) + "`)";
        return this;
    }

    public Table addInt(String name) {
        return addColumn(name, "INT");
    }
    public Table addInt(String name, Integer def) {
        return addColumn(name, "INT DEFAULT " + (def == null ? "NULL" : def));
    }

    public Table addLong(String name) {
        return addBigInt(name);
    }
    public Table addLong(String name, long def) {
        return addBigInt(name, def);
    }

    public Table addBigInt(String name) {
        return addColumn(name, "BIGINT");
    }
    public Table addBigInt(String name, long def) {
        return addColumn(name, "BIGINT DEFAULT " + def);
    }

    public Table addDouble(String name) {
        return addColumn(name, "DOUBLE");
    }
    public Table addDouble(String name, double def) {
        return addColumn(name, "DOUBLE DEFAULT " + def);
    }

    public Table addBoolean(String name) {
        return addColumn(name, "BOOLEAN");
    }
    public Table addBoolean(String name, boolean def) {
        return addColumn(name, "BOOLEAN DEFAULT " + def);
    }

    public Table addVarchar(String name, int length) {
        return addColumn(name, "VARCHAR(" + length + ")");
    }

    public Table addDate(String name) {
        return addColumn(name, "DATE");
    }

    public Table addText(String name) {
        return addColumn(name, "TEXT");
    }
    public Table addText(String name, String def) {
        return addColumn(name, "TEXT DEFAULT " + def);
    }

    public Table addFloat(String name) {
        return addColumn(name, "FLOAT");
    }
    public Table addFloat(String name, float def) {
        return addColumn(name, "FLOAT DEFAULT " + def);
    }

    public Table addIndex(String indexName, String column) {
        indexes.put(indexName, "INDEX " + indexName + " (`" + column + "`)");
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
            commands.put(entry.getKey(), "ALTER TABLE `" + name + "` ADD " + entry.getValue());
        }

        return commands;
    }

}
