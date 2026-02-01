package de.mrtesz.dbutils.utils.mariadb;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class MariaDBTable {

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
    public MariaDBTable(String name) {
        this.name = name;
    }

    private MariaDBTable addColumn(String columnName, String definition) {
        columns.put(columnName, definition);
        return this;
    }

    /**
     * Set the Primary Keys of the Table
     * @param keys The keys, wich should be turned into Primarys
     */
    public MariaDBTable setPrimaryKeys(String... keys) {
        primaryKeyColumns.clear();
        primaryKeyColumns.addAll(Arrays.stream(keys)
                .filter(columns::containsKey)
                .distinct()
                .toList());
        return this;
    }

    public MariaDBTable setUnique(String... unique) {
        this.unique = "UNIQUE(`" + String.join("`, `", unique) + "`)";
        return this;
    }

    public MariaDBTable addInt(String name) {
        return addColumn(name, "INT");
    }
    public MariaDBTable addInt(String name, Integer def) {
        return addColumn(name, "INT DEFAULT " + (def == null ? "NULL" : def));
    }

    public MariaDBTable addLong(String name) {
        return addBigInt(name);
    }
    public MariaDBTable addLong(String name, long def) {
        return addBigInt(name, def);
    }

    public MariaDBTable addBigInt(String name) {
        return addColumn(name, "BIGINT");
    }
    public MariaDBTable addBigInt(String name, long def) {
        return addColumn(name, "BIGINT DEFAULT " + def);
    }

    public MariaDBTable addDate(String name) {
        return addColumn(name, "DATE");
    }

    public MariaDBTable addDouble(String name) {
        return addColumn(name, "DOUBLE");
    }
    public MariaDBTable addDouble(String name, double def) {
        return addColumn(name, "DOUBLE DEFAULT " + def);
    }

    public MariaDBTable addFloat(String name) {
        return addColumn(name, "FLOAT");
    }
    public MariaDBTable addFloat(String name, float def) {
        return addColumn(name, "FLOAT DEFAULT " + def);
    }

    public MariaDBTable addBoolean(String name) {
        return addColumn(name, "BOOLEAN");
    }
    public MariaDBTable addBoolean(String name, boolean def) {
        return addColumn(name, "BOOLEAN DEFAULT " + def);
    }

    public MariaDBTable addVarchar(String name, int length) {
        return addColumn(name, "VARCHAR(" + length + ")");
    }

    public MariaDBTable addText(String name) {
        return addColumn(name, "TEXT");
    }
    public MariaDBTable addText(String name, String def) {
        return addColumn(name, "TEXT DEFAULT " + def);
    }

    public MariaDBTable addMediumText(String name) {
        return addColumn(name, "MEDIUMTEXT");
    }
    public MariaDBTable addMediumText(String name, String def) {
        return addColumn(name, "MEDIUMTEXT DEFAULT " + def);
    }

    public MariaDBTable addLongText(String name) {
        return addColumn(name, "LONGTEXT");
    }
    public MariaDBTable addLongText(String name, String def) {
        return addColumn(name, "LONGTEXT DEFAULT " + def);
    }

    public MariaDBTable addIndex(String indexName, String column) {
        indexes.put(indexName, "INDEX " + indexName + " (`" + column + "`)");
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

        for (String index : indexes.values()) {
            sb.append(", ").append(index);
        }

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
            commands.put(entry.getKey(), "ALTER TABLE `" + name + "` ADD " + entry.getValue());
        }

        return commands;
    }

}
