package io.github.mrtesz.teszcore.exceptions;

import org.jetbrains.annotations.Nullable;

/** Exception, signalizing an object was not null when should be */
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
