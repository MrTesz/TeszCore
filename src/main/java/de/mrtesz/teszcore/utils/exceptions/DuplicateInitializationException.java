package de.mrtesz.teszcore.utils.exceptions;

import de.mrtesz.teszcore.utils.copyable.Copyable;

public class DuplicateInitializationException extends TeszCoreException implements Copyable<DuplicateInitializationException> {

    public DuplicateInitializationException(String message) {
        super(message);
    }

    @Override
    public DuplicateInitializationException copy() {
        return new DuplicateInitializationException(this.message);
    }
}
