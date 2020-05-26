package ru.javaops.masterjava.processors;

import com.google.common.collect.Multimap;
import ru.javaops.masterjava.model.Agregator;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.CrossProjectGroupDao;
import ru.javaops.masterjava.persist.dao.CrossUserGroupDao;
import ru.javaops.masterjava.persist.model.CrossTable;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.xml.schema.Project;


import java.util.*;

public class CrossTableProcessor {

    public void process(Agregator agregator){
        Multimap<Integer, String> userGroupMap = agregator.getUserGroupMap();
        List<Group> groupList = agregator.getGroupList();
        Multimap<Integer, Project.Group> projectGroupMap = agregator.getProjectGroupMap();
        List<CrossTable> listUserGroup = new ArrayList<>();
        List<CrossTable> listProjectGroup = new ArrayList<>();

        Map<Integer, Collection<String>> mapUser = userGroupMap.asMap();
        for (Map.Entry<Integer, Collection<String>> pair : mapUser.entrySet()) {
            Integer userId = pair.getKey();
            List<String> list = (List<String>) pair.getValue();
            list.forEach(groupStr -> {
                Integer groupId = groupList.stream().filter(group -> groupStr.equals(group.getName()))
                        .mapToInt(group -> group.getId()).findFirst().getAsInt();
                if (groupId != null) {
                    listUserGroup.add(new CrossTable(userId, groupId));
                }

            });
        }
        saveCrossUserGroup(listUserGroup);

        Map<Integer, Collection<Project.Group>> mapGroup = projectGroupMap.asMap();
        for (Map.Entry<Integer, Collection<Project.Group>> pair : mapGroup.entrySet()) {
            Integer projectId = pair.getKey();
            List<Project.Group> list = (List<Project.Group>) pair.getValue();
            list.forEach(groupStr -> {
                Integer groupId = groupList.stream().filter(group -> Objects.equals(groupStr.getName(),group.getName()))
                        .mapToInt(group -> group.getId()).findFirst().getAsInt();
                if (groupId != null) {
                    listProjectGroup.add(new CrossTable(projectId, groupId));
                }
            });
        }
        saveCrossProjectGroup(listProjectGroup);
    }

    private void saveCrossProjectGroup(List<CrossTable> listProjectGroup) {
        CrossProjectGroupDao dao = DBIProvider.getDao(CrossProjectGroupDao.class);
        dao.insertBatch(listProjectGroup);

    }

    private void saveCrossUserGroup(List<CrossTable> listUserGroup) {
        CrossUserGroupDao dao = DBIProvider.getDao(CrossUserGroupDao.class);
        dao.insertBatch(listUserGroup);
    }
}
