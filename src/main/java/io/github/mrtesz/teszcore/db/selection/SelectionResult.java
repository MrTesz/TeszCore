package io.github.mrtesz.teszcore.db.selection;

import io.github.mrtesz.teszcore.copyable.Copyable;
import io.github.mrtesz.teszcore.exceptions.DatabaseException;
import lombok.AllArgsConstructor;

import java.sql.Date;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
public class SelectionResult implements Copyable<SelectionResult> {

    private final Map<String, Object> result;

    public Object get(String s) {
        return result.get(s);
    }

    public String getString(String s) throws DatabaseException {
        Object o = result.get(s);

        if (o instanceof String s1)
            return s1;
        if (o == null)
            return null;

        throw new DatabaseException("result.get(" + s + ") was not a String! Was instead: " + o + " Type: " + o.getClass().getName());
    }
    public String getString(String s, String def) {
        Object o = result.get(s);

        if (o instanceof String s1)
            return s1;

        return def;
    }

    public Integer getInt(String s) throws DatabaseException {
        Object o = result.get(s);

        if (o instanceof Integer i)
            return i;
        if (o == null)
            return null;

        throw new DatabaseException("result.get(" + s + ") was not an Integer! Was instead: " + o + " Type: " + o.getClass().getName());
    }
    public Integer getInt(String s, Integer def) {
        Object o = result.get(s);

        if (o instanceof Integer i)
            return i;

        return def;
    }

    public Long getLong(String s) throws DatabaseException {
        Object o = result.get(s);

        if (o instanceof Long l)
            return l;
        if (o == null)
            return null;

        throw new DatabaseException("result.get(" + s + ") was not a Long! Was instead: " + o + " Type: " + o.getClass().getName());
    }
    public Long getLong(String s, Long def) {
        Object o = result.get(s);

        if (o instanceof Long l)
            return l;

        return def;
    }

    public Double getDouble(String s) throws DatabaseException {
        Object o = result.get(s);

        if (o instanceof Double d)
            return d;
        if (o == null)
            return null;

        throw new DatabaseException("result.get(" + s + ") was not a Double! Was instead: " + o + " Type: " + o.getClass().getName());
    }
    public Double getDouble(String s, Double def) {
        Object o = result.get(s);

        if (o instanceof Double d)
            return d;

        return def;
    }

    public Float getFloat(String s) throws DatabaseException {
        Object o = result.get(s);

        if (o instanceof Float f)
            return f;
        if (o == null)
            return null;

        throw new DatabaseException("result.get(" + s + ") was not a Float! Was instead: " + o + " Type: " + o.getClass().getName());
    }
    public Float getFloat(String s, Float def) {
        Object o = result.get(s);

        if (o instanceof Float f)
            return f;

        return def;
    }

    public Boolean getBoolean(String s) throws DatabaseException {
        Object o = result.get(s);

        if (o instanceof Boolean b)
            return b;
        if (o == null)
            return null;

        throw new DatabaseException("result.get(" + s + ") was not a Boolean! Was instead: " + o + " Type: " + o.getClass().getName());
    }
    public Boolean getBoolean(String s, Boolean def) {
        Object o = result.get(s);

        if (o instanceof Boolean b)
            return b;

        return def;
    }

    public Date getDate(String s) throws DatabaseException {
        Object o = result.get(s);

        if (o instanceof Date d)
            return d;
        if (o == null)
            return null;

        throw new DatabaseException("result.get(" + s + ") was not a Date! Was instead: " + o + " Type: " + o.getClass().getName());
    }
    public Date getDate(String s, Date def) {
        Object o = result.get(s);

        if (o instanceof Date d)
            return d;

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
