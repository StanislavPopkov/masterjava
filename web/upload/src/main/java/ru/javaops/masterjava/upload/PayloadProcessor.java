package ru.javaops.masterjava.upload;

import lombok.AllArgsConstructor;
import lombok.val;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.Project;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class PayloadProcessor {
    private final CityProcessor cityProcessor = new CityProcessor();
    private final UserProcessor userProcessor = new UserProcessor();
    private final ProjectProcessor projectProcessor = new ProjectProcessor();
    private final GroupProcessor groupProcessor = new GroupProcessor();
    private final CrossTableProcessor crossTableProcessor = new CrossTableProcessor();

    @AllArgsConstructor
    public static class FailedEmails {
        public String emailsOrRange;
        public String reason;

        @Override
        public String toString() {
            return emailsOrRange + " : " + reason;
        }
    }


    public List<FailedEmails> process(InputStream is, int chunkSize) throws XMLStreamException, JAXBException, IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        byte[] xmlArray = result.toByteArray();

        StaxStreamProcessor processor = new StaxStreamProcessor(new ByteArrayInputStream(xmlArray));
        Map<String, Project> projects = projectProcessor.process(processor);
        processor = new StaxStreamProcessor(new ByteArrayInputStream(xmlArray));
        Map<String, Group> groups = groupProcessor.process(processor, projects);
        val cities = cityProcessor.process(processor);
        val userFail = userProcessor.process(processor, cities, chunkSize);
        processor = new StaxStreamProcessor(new ByteArrayInputStream(xmlArray));
        crossTableProcessor.process(processor, groups);
        return userFail;
    }
}