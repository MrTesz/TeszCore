package io.github.mrtesz.teszcore.exceptions;

import org.jetbrains.annotations.Nullable;

public class NotNullException extends TeszCoreException {

    public NotNullException() {
        super();
    }
    public NotNullException(@Nullable String message) {
        super(message);
    }

    @Override
    public TeszCoreException copy() {
        return new NotNullException(message);
    }
}
