package ru.javaops.masterjava.upload;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.ProjectDao;
import ru.javaops.masterjava.persist.model.Project;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.Map;

@Slf4j
public class ProjectProcessor {

    private final ProjectDao projectDao = DBIProvider.getDao(ProjectDao.class);

    public Map<String, Project> process(StaxStreamProcessor processor) throws XMLStreamException {
        val map = projectDao.getAsMap();
        val projects = new ArrayList<Project>();
        while (processor.doUntil(XMLEvent.START_ELEMENT, "Project")) {
            val projectName = processor.getAttribute("name");
            val projectDescription = processor.getElementValue("description");
            if (!map.containsKey(projectName)) {
                projects.add(new Project(projectName, projectDescription));
            }
        }

        log.info("Insert batch " + projects);
        projectDao.insertBatch(projects);
        return projectDao.getAsMap();
    }
}