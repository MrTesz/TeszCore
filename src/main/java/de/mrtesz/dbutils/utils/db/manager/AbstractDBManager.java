package de.mrtesz.dbutils.utils.db.manager;

import de.mrtesz.dbutils.api.db.manager.DBManager;

public abstract class AbstractDBManager implements DBManager {

    private boolean closed = false;

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() {
        this.closed = true;
    }
}
