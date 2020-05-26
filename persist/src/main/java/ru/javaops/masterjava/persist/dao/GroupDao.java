package ru.javaops.masterjava.persist.dao;

import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.model.Group;

import java.util.List;

public abstract class GroupDao implements AbstractDao {

    @SqlUpdate("TRUNCATE groups")
    @Override
    public abstract void clean();

    public Group insert(Group group) {
        if (group.isNew()) {
            int id = insertGeneratedId(group);
            group.setId(id);
        } else {
            insertWitId(group);
        }
        return group;
    }

    @SqlQuery("SELECT nextval('user_seq')")
    abstract int getNextVal();

    @Transaction
    public int getSeqAndSkip(int step) {
        int id = getNextVal();
        DBIProvider.getDBI().useHandle(h -> h.execute("ALTER SEQUENCE user_seq RESTART WITH " + (id + step)));
        return id;
    }

    @SqlUpdate("INSERT INTO groups (group_name, type) VALUES (:name, CAST(:type AS group_type)) ON CONFLICT DO NOTHING")
    @GetGeneratedKeys
    abstract int insertGeneratedId(@BindBean Group group);

    @SqlUpdate("INSERT INTO groups (id, group_name, type) VALUES (:id, :name, CAST(:type AS group_type)) ON CONFLICT DO NOTHING ")
    abstract void insertWitId(@BindBean Group group);

    @SqlBatch("INSERT INTO groups (group_name, type) VALUES (:name, CAST(:type AS group_type)) ON CONFLICT DO NOTHING")
    @BatchChunkSize(20)
    public abstract int[] insertBatch(@BindBean List<Group> groupList);
}
