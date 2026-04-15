package io.github.mrtesz.teszcore.exceptions;

import org.jetbrains.annotations.Nullable;

public class DatabaseException extends TeszCoreException {

    public DatabaseException(@Nullable String message) {
        super(message);
    }

    @Override
    public DatabaseException copy() {
        return new DatabaseException(this.message);
    }
}
