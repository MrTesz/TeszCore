package io.github.mrtesz.teszcore.db.selection;

import io.github.mrtesz.teszcore.copyable.Copyable;
import io.github.mrtesz.teszcore.exceptions.DatabaseException;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
public class SelectionResult implements Copyable<SelectionResult> {

    private final @NotNull Map<String, Object> result;

    public @Nullable Object get(@NotNull String columnName) {
        return result.get(columnName);
    }
    public @Nullable Object get(@NotNull String columnName, Object def) {
        return result.getOrDefault(columnName, def);
    }

    public @Nullable String getString(@NotNull String columnName) throws DatabaseException {
        Object o = result.get(columnName);

        if (o instanceof String s)
            return s;
        if (o == null)
            return null;

        throw new DatabaseException("result.get(" + columnName + ") was not a String! Was instead: " + o + " Type: " + o.getClass().getName());
    }
    public @Nullable String getString(@NotNull String columnName, String def) {
        Object o = result.get(columnName);

        if (o instanceof String s)
            return s;

        return def;
    }

    public @Nullable Integer getInt(@NotNull String columnName) throws DatabaseException {
        Object o = result.get(columnName);

        if (o instanceof Integer i)
            return i;
        if (o == null)
            return null;

        throw new DatabaseException("result.get(" + columnName + ") was not an Integer! Was instead: " + o + " Type: " + o.getClass().getName());
    }
    public @Nullable Integer getInt(@NotNull String columnName, Integer def) {
        Object o = result.get(columnName);

        if (o instanceof Integer i)
            return i;

        return def;
    }

    public @Nullable Long getLong(@NotNull String columnName) throws DatabaseException {
        Object o = result.get(columnName);

        if (o instanceof Long l)
            return l;
        if (o == null)
            return null;

        throw new DatabaseException("result.get(" + columnName + ") was not a Long! Was instead: " + o + " Type: " + o.getClass().getName());
    }
    public @Nullable Long getLong(@NotNull String columnName, Long def) {
        Object o = result.get(columnName);

        if (o instanceof Long l)
            return l;

        return def;
    }

    public @Nullable Double getDouble(@NotNull String columnName) throws DatabaseException {
        Object o = result.get(columnName);

        if (o instanceof Double d)
            return d;
        if (o == null)
            return null;

        throw new DatabaseException("result.get(" + columnName + ") was not a Double! Was instead: " + o + " Type: " + o.getClass().getName());
    }
    public @Nullable Double getDouble(@NotNull String columnName, Double def) {
        Object o = result.get(columnName);

        if (o instanceof Double d)
            return d;

        return def;
    }

    public @Nullable Float getFloat(@NotNull String columnName) throws DatabaseException {
        Object o = result.get(columnName);

        if (o instanceof Float f)
            return f;
        if (o == null)
            return null;

        throw new DatabaseException("result.get(" + columnName + ") was not a Float! Was instead: " + o + " Type: " + o.getClass().getName());
    }
    public @Nullable Float getFloat(@NotNull String columnName, Float def) {
        Object o = result.get(columnName);

        if (o instanceof Float f)
            return f;

        return def;
    }

    public @Nullable Boolean getBoolean(@NotNull String columnName) throws DatabaseException {
        Object o = result.get(columnName);

        if (o instanceof Boolean b)
            return b;
        if (o == null)
            return null;

        throw new DatabaseException("result.get(" + columnName + ") was not a Boolean! Was instead: " + o + " Type: " + o.getClass().getName());
    }
    public @Nullable Boolean getBoolean(@NotNull String columnName, Boolean def) {
        Object o = result.get(columnName);

        if (o instanceof Boolean b)
            return b;

        return def;
    }

    public @Nullable Date getDate(@NotNull String columnName) throws DatabaseException {
        Object o = result.get(columnName);

        if (o instanceof Date d)
            return d;
        if (o == null)
            return null;

        throw new DatabaseException("result.get(" + columnName + ") was not a Date! Was instead: " + o + " Type: " + o.getClass().getName());
    }
    public @Nullable Date getDate(@NotNull String columnName, Date def) {
        Object o = result.get(columnName);

        if (o instanceof Date d)
            return d;

        return def;
    }

    public @Nullable Timestamp getTimestamp(@NotNull String columnName) throws DatabaseException {
        Object o = result.get(columnName);

        if (o instanceof Timestamp t)
            return t;
        if (o == null)
            return null;

        throw new DatabaseException("result.get(" + columnName + ") was not a Timestamp! Was instead: " + o + " Type: " + o.getClass().getName());
    }
    public @Nullable Timestamp getTimestamp(@NotNull String columnName, Timestamp def) {
        Object o = result.get(columnName);

        if (o instanceof Timestamp t)
            return t;

        return def;
    }

    public boolean isEmpty() {
        return result.isEmpty();
    }

    public Set<Map.Entry<String, Object>> entrySet() {
        return result.entrySet();
    }
    public Map<String, Object> asMap() {
        return result;
    }

    @Override
    public SelectionResult copy() {
        return new SelectionResult(this.result);
    }
}
