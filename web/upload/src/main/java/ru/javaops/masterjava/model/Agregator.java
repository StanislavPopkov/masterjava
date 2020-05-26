package ru.javaops.masterjava.model;

import com.google.common.collect.Multimap;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.xml.schema.Project;

import java.util.List;

public class Agregator {

    Multimap<Integer, String> userGroupMap;
    Multimap<Integer, Project.Group> projectGroupMap;
    private List<Group> groupList;

    public Multimap<Integer, String> getUserGroupMap() {
        return userGroupMap;
    }

    public void setUserGroupMap(Multimap<Integer, String> userGroupMap) {
        this.userGroupMap = userGroupMap;
    }

    public Multimap<Integer, Project.Group> getProjectGroupMap() {
        return projectGroupMap;
    }

    public void setProjectGroupMap(Multimap<Integer, Project.Group> projectGroupMap) {
        this.projectGroupMap = projectGroupMap;
    }

    public List<Group> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<Group> groupList) {
        this.groupList = groupList;
    }
}
