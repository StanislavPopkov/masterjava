package ru.javaops.masterjava.processors;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import ru.javaops.masterjava.model.Agregator;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.ProjectDao;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.Project;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.JaxbUnmarshaller;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class ProjectProcessor {
    private static final JaxbParser jaxbParser = new JaxbParser(ObjectFactory.class);
    private static ProjectDao projectDao = DBIProvider.getDao(ProjectDao.class);

    public List<Project> process(byte[] bytes, Agregator agregator) throws XMLStreamException, JAXBException {
        List<Project> projectList = new ArrayList<>();
        Multimap<Integer, ru.javaops.masterjava.xml.schema.Project.Group> map = ArrayListMultimap.create();
        JaxbUnmarshaller unmarshaller = jaxbParser.createUnmarshaller();
        int id = projectDao.getNextVal();
        try (StaxStreamProcessor processor =
                     new StaxStreamProcessor(new ByteArrayInputStream(bytes))) {
            XMLStreamReader reader = processor.getReader();
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLEvent.START_ELEMENT && "Project".equals(reader.getLocalName())) {
                    ru.javaops.masterjava.xml.schema.Project xmlProject = unmarshaller.unmarshal(reader, ru.javaops.masterjava.xml.schema.Project.class);
                    Project project = new Project(id, xmlProject.getDescription(), xmlProject.getName());
                    projectList.add(project);
                    List<ru.javaops.masterjava.xml.schema.Project.Group> groupList = xmlProject.getGroup();
                    if (groupList != null && !groupList.isEmpty()) {
                        map.putAll(id,  xmlProject.getGroup());
                    }
                    id++;
                }
            }
        }
        agregator.setProjectGroupMap(map);
        return saveGroups(projectList);
    }

    private List<Project> saveGroups(List<Project> projectList) {
        List<Project> groupsWithId = new ArrayList<>();
        projectList.forEach(project -> groupsWithId.add(projectDao.insert(project)));
        return groupsWithId;
    }

}
