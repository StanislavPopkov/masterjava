package ru.javaops.masterjava.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;

public class Group extends BaseEntity {

    @Column("group_name")
    private String name;
    private GroupTypePersist type;

    public Group(Integer id, String name, GroupTypePersist groupType) {
        super(id);
        this.name = name;
        this.type = groupType;
    }

    public Group(String name, GroupTypePersist groupType) {
        this(null, name, groupType);
    }

    public Group(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GroupTypePersist getType() {
        return type;
    }

    public void setType(GroupTypePersist type) {
        this.type = type;
    }
}
