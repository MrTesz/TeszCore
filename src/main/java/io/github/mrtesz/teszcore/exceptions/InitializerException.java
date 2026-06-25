package io.github.mrtesz.teszcore.exceptions;

import io.github.mrtesz.teszcore.logged.Initializer;
import org.jetbrains.annotations.Nullable;

/// Thrown when {@link Initializer} throws an error
public class InitializerException extends TeszCoreException {

    public InitializerException(@Nullable String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public TeszCoreException copy() {
        return new InitializerException(message, cause);
    }
}
