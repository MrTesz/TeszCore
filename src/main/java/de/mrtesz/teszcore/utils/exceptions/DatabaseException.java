package de.mrtesz.teszcore.utils.exceptions;

import de.mrtesz.teszcore.utils.copyable.Copyable;

public class DatabaseException extends TeszCoreException implements Copyable<DatabaseException> {

    public DatabaseException(String message) {
        super(message);
    }

    @Override
    public DatabaseException copy() {
        return new DatabaseException(this.message);
    }
}
