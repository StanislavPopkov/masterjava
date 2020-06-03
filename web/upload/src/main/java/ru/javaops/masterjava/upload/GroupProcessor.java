package ru.javaops.masterjava.upload;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.GroupDao;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.Project;
import ru.javaops.masterjava.persist.model.type.GroupType;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.Map;

@Slf4j
public class GroupProcessor {

    private final GroupDao groupDao = DBIProvider.getDao(GroupDao.class);

    public Map<String, Group> process(StaxStreamProcessor processor, Map<String, Project> projectMap) throws XMLStreamException {
        val map = groupDao.getAsMap();
        val groups = new ArrayList<Group>();

        while (processor.doUntil(XMLEvent.START_ELEMENT, "Project")) {
            val projectName = processor.getAttribute("name");
            while (processor.doUntil(XMLEvent.START_ELEMENT, "Group")) {
                val groupName = processor.getAttribute("name");
                Integer projectId = projectMap.get(projectName).getId();
                if (!map.containsKey(groupName) && projectId != null) {
                    groups.add(new Group(groupName, GroupType.valueOf(processor.getAttribute( "type")), projectId));
                }
            }
        }

        log.info("Insert batch " + groups);
        groupDao.insertBatch(groups);
        return groupDao.getAsMap();
    }
}