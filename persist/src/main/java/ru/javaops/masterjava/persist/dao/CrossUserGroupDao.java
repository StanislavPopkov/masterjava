package ru.javaops.masterjava.persist.dao;

import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import ru.javaops.masterjava.persist.model.CrossTable;

import java.util.List;

public abstract class CrossUserGroupDao implements AbstractDao {

    @SqlUpdate("INSERT INTO cross_user_group (id1, id2) VALUES (:id1, :id2)")
    abstract void insertWitId(@BindBean CrossTable crossTable);

    @SqlBatch("INSERT INTO cross_user_group (id1, id2) VALUES (:id1, :id2)")
    @BatchChunkSize(20)
    public abstract int[] insertBatch(@BindBean List<CrossTable> tableList);
}
