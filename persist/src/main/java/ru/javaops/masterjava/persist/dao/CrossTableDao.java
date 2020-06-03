package ru.javaops.masterjava.persist.dao;

import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import ru.javaops.masterjava.persist.model.CrossTable;

public abstract class CrossTableDao implements AbstractDao {

    @SqlUpdate("INSERT INTO user_group (user_id, group_id) VALUES (:userId, :groupId)")
    public abstract void insert(@BindBean CrossTable crossTable);
}