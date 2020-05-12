package ru.javaops.masterjava.service;

import ru.javaops.masterjava.xml.schema.FlagType;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class FileUploadService {

    public List<User> getUserList(byte[] file) {
        List<User> userList = new ArrayList<>();
        try (StaxStreamProcessor processor =
                     new StaxStreamProcessor(new ByteArrayInputStream(file))) {
            XMLStreamReader reader = processor.getReader();
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLEvent.START_ELEMENT && "User".equals(reader.getLocalName())) {
                    User user = new User();
                    user.setFlag(FlagType.valueOf(reader.getAttributeValue("", "flag").toUpperCase()));
                    user.setEmail(reader.getAttributeValue("", "email"));
                    user.setValue(reader.getElementText());
                    userList.add(user);
                }
            }

        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        return userList;
    }
}
