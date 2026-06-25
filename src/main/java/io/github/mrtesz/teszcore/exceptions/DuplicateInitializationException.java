package io.github.mrtesz.teszcore.exceptions;

import org.jetbrains.annotations.Nullable;

/** Exception, signalizing a class is initialized two times when must only be initialized once */
public class DuplicateInitializationException extends TeszCoreException {

    public DuplicateInitializationException(@Nullable String message) {
        super(message);
    }

    @Override
    public DuplicateInitializationException copy() {
        return new DuplicateInitializationException(this.message);
    }
}
