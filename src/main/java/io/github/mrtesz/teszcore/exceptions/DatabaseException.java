package io.github.mrtesz.teszcore.exceptions;

public class DatabaseException extends TeszCoreException {

    public DatabaseException(String message) {
        super(message);
    }

    @Override
    public DatabaseException copy() {
        return new DatabaseException(this.message);
    }
}
