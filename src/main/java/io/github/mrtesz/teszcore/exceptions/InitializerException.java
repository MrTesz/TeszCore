package io.github.mrtesz.teszcore.exceptions;

import org.jetbrains.annotations.Nullable;

public class InitializerException extends TeszCoreException {

    public InitializerException(@Nullable String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public TeszCoreException copy() {
        return new InitializerException(message, cause);
    }
}
