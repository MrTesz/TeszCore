package de.mrtesz.dbutils.utils.copyable;

public interface Copyable<T extends Copyable<T>> {
    T copy();
}
