package io.github.mrtesz.teszcore.copyable;

/** Interface, signalizing, the subclass can be copied with {@link #copy()} */
public interface Copyable<T extends Copyable<T>> {
    /**
     * Copy the executing class
     * @return a copy of the class executed in
     */
    T copy();
}