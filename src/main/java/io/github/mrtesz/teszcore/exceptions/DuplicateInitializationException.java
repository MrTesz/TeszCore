package io.github.mrtesz.teszcore.exceptions;

public class DuplicateInitializationException extends TeszCoreException {

    public DuplicateInitializationException(String message) {
        super(message);
    }

    @Override
    public DuplicateInitializationException copy() {
        return new DuplicateInitializationException(this.message);
    }
}
