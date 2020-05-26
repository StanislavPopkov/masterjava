package ru.javaops.masterjava.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;

import java.util.List;
import java.util.Objects;

public class Project extends BaseEntity {

    private String description;

    @Column("project_name")
    private String name;

    public Project(Integer id, String description, String name) {
        super(id);
        this.description = description;
        this.name = name;
    }

    public Project(String description, String name) {
        this(null, description, name);
    }

    public Project(){

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Project project = (Project) o;
        return description.equals(project.description) &&
                name.equals(project.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), description, name);
    }
}
