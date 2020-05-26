package ru.javaops.masterjava.persist.dao;

import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import ru.javaops.masterjava.persist.model.Project;

import java.util.List;

public abstract class ProjectDao implements AbstractDao{

    @SqlUpdate("TRUNCATE projects")
    @Override
    public abstract void clean();

    public Project insert(Project project) {
        if (project.isNew()) {
            int id = insertGeneratedId(project);
            project.setId(id);
        } else {
            insertWitId(project);
        }
        return project;
    }

    @SqlQuery("SELECT nextval('user_seq')")
    public abstract int getNextVal();

    @SqlUpdate("INSERT INTO projects (project_name, description) VALUES (:name, :description) ON CONFLICT DO NOTHING")
    @GetGeneratedKeys
    abstract int insertGeneratedId(@BindBean Project project);

    @SqlUpdate("INSERT INTO projects (id, project_name, description) VALUES (:id, :name, :description) ON CONFLICT DO NOTHING ")
    abstract void insertWitId(@BindBean Project project);

    @SqlBatch("INSERT INTO projects (project_name, description) VALUES (:name, :description) ON CONFLICT DO NOTHING")
    @BatchChunkSize(20)
    public abstract int[] insertBatch(@BindBean List<Project> projectList);
}
