package de.mrtesz.teszcore.utils.exceptions;

import de.mrtesz.teszcore.utils.copyable.Copyable;

public class NotInitializedException extends TeszCoreException implements Copyable<NotInitializedException> {
    public NotInitializedException(String message) {
        super(message);
    }

    @Override
    public NotInitializedException copy() {
        return new NotInitializedException(this.message);
    }
}
