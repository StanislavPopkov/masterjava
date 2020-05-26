package ru.javaops.masterjava.service;

import ru.javaops.masterjava.model.Agregator;
import ru.javaops.masterjava.persist.model.City;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.Project;
import ru.javaops.masterjava.processors.*;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.util.List;

public class UploadServiceImpl implements UploadService {
    UserProcessor userProcessor = new UserProcessor();
    CityProcessor cityProcessor = new CityProcessor();
    GroupProcessor groupProcessor = new GroupProcessor();
    ProjectProcessor projectProcessor = new ProjectProcessor();
    CrossTableProcessor crossTableProcessor = new CrossTableProcessor();

    @Override
    public List<UserProcessor.FailedEmails> process(byte[] xmlArray, int chunkSize) {
        Agregator agregator = new Agregator();
        List<City> cities = null;
        List<UserProcessor.FailedEmails> failedEmailsList = null;
        List<Group> groupList = null;
        List<Project> projectList = null;
        try {
            cities = cityProcessor.process(xmlArray);
            failedEmailsList = userProcessor.process(xmlArray, chunkSize, cities, agregator);
            groupList = groupProcessor.process(xmlArray);
            agregator.setGroupList(groupList);
            projectList = projectProcessor.process(xmlArray, agregator);
            crossTableProcessor.process(agregator);
        } catch (XMLStreamException | JAXBException e) {
            e.printStackTrace();
        }

        return failedEmailsList;
    }
}
