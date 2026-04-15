package io.github.mrtesz.teszcore.exceptions;

import org.jetbrains.annotations.Nullable;

public class DuplicateInitializationException extends TeszCoreException {

    public DuplicateInitializationException(@Nullable String message) {
        super(message);
    }

    @Override
    public DuplicateInitializationException copy() {
        return new DuplicateInitializationException(this.message);
    }
}
