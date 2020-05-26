package ru.javaops.masterjava.processors;

import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.GroupDao;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.GroupTypePersist;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class GroupProcessor {

    public List<Group> process(byte[] bytes) throws XMLStreamException {
        List<Group> groupList = new ArrayList<>();
        try (StaxStreamProcessor processor =
                     new StaxStreamProcessor(new ByteArrayInputStream(bytes))) {
            XMLStreamReader reader = processor.getReader();
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLEvent.START_ELEMENT && "Group".equals(reader.getLocalName())) {
                    Group group = new Group();
                    group.setName(reader.getAttributeValue("", "name"));
                    group.setType(GroupTypePersist.valueOf(reader.getAttributeValue("", "type")));
                    groupList.add(group);
                }
            }
        }
        return saveGroups(groupList);
    }

    private List<Group> saveGroups(List<Group> groupList) {
        GroupDao dao = DBIProvider.getDao(GroupDao.class);
        List<Group> groupsWithId = new ArrayList<>();
        groupList.forEach(group -> groupsWithId.add(dao.insert(group)));
        return groupsWithId;
    }

}
