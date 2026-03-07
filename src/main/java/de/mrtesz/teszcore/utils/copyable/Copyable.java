package de.mrtesz.teszcore.utils.copyable;

public interface Copyable<T extends Copyable<T>> {
    T copy();
}
