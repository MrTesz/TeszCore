package io.github.mrtesz.teszcore.exceptions;

public class NotInitializedException extends TeszCoreException {
    public NotInitializedException(String message) {
        super(message);
    }

    @Override
    public NotInitializedException copy() {
        return new NotInitializedException(this.message);
    }
}
