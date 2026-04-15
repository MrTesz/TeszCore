package io.github.mrtesz.teszcore.api.db.table;

import io.github.mrtesz.teszcore.copyable.Copyable;

import java.util.Map;

public interface DBTable extends Copyable<DBTable> {

    String getName();

    DBTable addColumn(String columnName, String definition);

    /**
     * Set the Primary Keys of the Table
     * @param keys The keys, wich should be turned into Primarys
     */
    DBTable setPrimaryKeys(String... keys);

    DBTable addUnique(String... unique);

    DBTable addInt(String name);
    DBTable addInt(String name, Integer def);

    DBTable addLong(String name);
    DBTable addLong(String name, long def);

    DBTable addBigInt(String name);
    DBTable addBigInt(String name, long def);

    DBTable addDouble(String name);
    DBTable addDouble(String name, double def);

    DBTable addFloat(String name);
    DBTable addFloat(String name, float def);

    DBTable addText(String name);
    DBTable addText(String name, String def);

    DBTable addIndex(String indexName, String column);

    String getCreateCommand();

    Map<String, String> getAlterColumnsCommands();
    Map<String, String> getAlterIndexCommands();
}
