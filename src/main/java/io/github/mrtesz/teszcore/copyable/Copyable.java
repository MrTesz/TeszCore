package io.github.mrtesz.teszcore.copyable;

public interface Copyable<T extends Copyable<T>> {
    T copy();
}