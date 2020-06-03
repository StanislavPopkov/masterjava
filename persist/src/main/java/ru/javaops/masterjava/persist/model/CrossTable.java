package ru.javaops.masterjava.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;

public class CrossTable {

    @Column("user_id")
    private int userId;
    @Column("group_id")
    private int groupId;

    public CrossTable(int userId, int groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }

    public CrossTable() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}