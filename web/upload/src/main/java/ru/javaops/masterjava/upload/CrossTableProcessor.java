package ru.javaops.masterjava.upload;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.CrossTableDao;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.CrossTable;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
public class CrossTableProcessor {
    private final UserDao userDao = DBIProvider.getDao(UserDao.class);
    private final CrossTableDao crossTableDao = DBIProvider.getDao(CrossTableDao.class);

    public void process(StaxStreamProcessor processor, Map<String, Group> groups) throws XMLStreamException {
        val projects = new ArrayList<CrossTable>();
        while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
            String groupRefs = processor.getAttribute("groupRefs");
            String userEmail = processor.getAttribute("email");
            List<String> groupList = null;
            if (groupRefs != null) {
                groupList = new ArrayList<>(Arrays.asList(groupRefs.split("\\s+")));
            }
            int userId = userDao.getByName(userEmail);
            if (groupList != null && userId != 0) {
                for (String groupName : groupList) {
                    Group group = groups.get(groupName);
                    if (group != null) {
                        crossTableDao.insert(new CrossTable(userId, group.getId()));
                    }
                }

            }
        }
        log.info("Insert batch " + projects);
    }
}
