package de.mrtesz.dbutils.utils.abstracts;

import lombok.Getter;

@Getter
public abstract class AbstractDBManager {

    private boolean closed = false;

    public void close() {
        this.closed = true;
    }
}
