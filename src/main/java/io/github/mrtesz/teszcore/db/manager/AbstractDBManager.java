package io.github.mrtesz.teszcore.db.manager;

import io.github.mrtesz.teszcore.api.db.manager.DBManager;

/// Abstract parent class of all DatabaseManagers
public abstract class AbstractDBManager implements DBManager {

    private boolean closed = false;

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() {
        disconnect();
        this.closed = true;
    }
}
