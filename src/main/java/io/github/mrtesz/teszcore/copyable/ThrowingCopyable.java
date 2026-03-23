package io.github.mrtesz.teszcore.copyable;

public interface ThrowingCopyable<T extends ThrowingCopyable<T>> {
    T copy();
}
