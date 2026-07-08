package io.github.mrtesz.teszcore.db.batch;

import io.github.mrtesz.teszcore.api.db.manager.AsyncDBManager;
import io.github.mrtesz.teszcore.api.db.manager.SyncDBManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * Container class for an SqlStatement for {@link SyncDBManager#executeBatchSql(List, String) SyncDBManager#executeBatchSql}
 * and {@link AsyncDBManager#executeBatchSql(List, String) AsyncDBManager#executeBatchSql}
 */
@Getter
@RequiredArgsConstructor
public class BatchSqlStatement {

    private final String sql;
    private final List<Object> params;

    public BatchSqlStatement(String sql) {
        this(sql, Collections.emptyList());
    }
}
