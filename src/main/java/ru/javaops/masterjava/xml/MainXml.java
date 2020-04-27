package ru.javaops.masterjava.xml;

import ru.javaops.masterjava.xml.schema.Group;
import ru.javaops.masterjava.xml.schema.Payload;
import ru.javaops.masterjava.xml.schema.Project;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.JaxbParser;

import javax.xml.bind.JAXBException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MainXml {

    private JaxbParser jaxbParser;

    public MainXml(JaxbParser jaxbParser) {
        this.jaxbParser = jaxbParser;
    }

    public List<String> userList(String projectName) throws IOException, JAXBException {
        Payload payload = jaxbParser.unmarshal(new FileInputStream("C:/Users/I am/Desktop/uchoba/j/masterJavaR/masterjava/src/main/resources/payload.xml"));
        List<String> resultList = new ArrayList<>();
        Project project = payload.getProjects().getProject().stream().filter(p -> projectName.equals(p.getProjectName()))
                .findAny().orElseThrow(() -> new IllegalArgumentException(String.format("project with name %s not found", projectName)));
        List<String> projectGroupList = project.getGroupList().getGroup().stream().map(group -> group.getGroupName()).collect(Collectors.toList());
        List<User> userList = payload.getUsers().getUser();
        userList.forEach(user -> {
            List<String> userGroup = user.getGroupList().getGroup().stream().map(group -> group.getGroupName()).collect(Collectors.toList());
            boolean result = Collections.disjoint(userGroup, projectGroupList);
            System.out.println(result);
         if (!result) {
             resultList.add(user.getFullName());
         }
        });
        return resultList;
    }
}
