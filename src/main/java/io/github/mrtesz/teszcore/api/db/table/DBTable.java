package io.github.mrtesz.teszcore.api.db.table;

import io.github.mrtesz.teszcore.copyable.Copyable;

import java.util.Map;

public interface DBTable extends Copyable<DBTable> {

    /**
     * Get the name of the table
     * @return name
     */
    String getName();

    /**
     * Add a column to the table
     * @param columnName name of the column
     * @param definition definition in SQL
     */
    DBTable addColumn(String columnName, String definition);

    /**
     * Set the Primary Keys of the Table
     * @param keys The keys, wich should be turned into Primarys
     */
    DBTable setPrimaryKeys(String... keys);

    /**
     * Add a unique key constellation
     * @param unique The constellation of keys to make unique
     */
    DBTable addUnique(String... unique);

    /**
     * Add an index
     * @param indexName The name of the index
     * @param column The column name to index
     */
    DBTable addIndex(String indexName, String column);

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

    /**
     * Create the SQL query to create the table
     * @return the SQL query to create the table
     */
    String getCreateCommand();

    /**
     * Create the SQL queries to alter columns of the table
     * @return the SQL queries to alter the columns of the table mapped by <columnName, command>
     */
    Map<String, String> getAlterColumnsCommands();
    /**
     * Create the SQL queries to alter indexes of the table
     * @return the SQL queries to alter the indexes of the table mapped by <indexName, command>
     */
    Map<String, String> getAlterIndexCommands();
}
