package ru.javaops.masterjava.processors;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ru.javaops.masterjava.model.Agregator;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.City;
import ru.javaops.masterjava.persist.model.User;
import ru.javaops.masterjava.persist.model.UserFlag;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class UserProcessor {
    private static final int NUMBER_THREADS = 4;

    private static final JaxbParser jaxbParser = new JaxbParser(ObjectFactory.class);
    private static UserDao userDao = DBIProvider.getDao(UserDao.class);

    private ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_THREADS);

    @AllArgsConstructor
    public static class FailedEmails {
        public String emailsOrRange;
        public String reason;

        @Override
        public String toString() {
            return emailsOrRange + " : " + reason;
        }
    }

    /*
     * return failed users chunks
     */
    public List<FailedEmails> process(final byte[] bytes, int chunkSize, List<City> cities, Agregator agregator) throws XMLStreamException, JAXBException {
        log.info("Start processing with chunkSize=" + chunkSize);

        Map<String, Future<List<String>>> chunkFutures = new LinkedHashMap<>();  // ordered map (emailRange -> chunk future)

        Multimap<Integer, String> map = ArrayListMultimap.create();
        int id = userDao.getSeqAndSkip(chunkSize);
        List<User> chunk = new ArrayList<>(chunkSize);
        val processor = new StaxStreamProcessor(new ByteArrayInputStream(bytes));
        val unmarshaller = jaxbParser.createUnmarshaller();

        while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
            String cityRef = processor.getAttribute("city");
            String groupRefs = processor.getAttribute("groupRefs");
            ru.javaops.masterjava.xml.schema.User xmlUser = unmarshaller.unmarshal(processor.getReader());

            Integer cityIdFromBase = cities.stream().filter(city -> cityRef.equals(city.getShortName()))
                    .mapToInt(city -> city.getId()).findFirst().orElseThrow(() -> new NoSuchElementException(String.format("City with shortName %s not found", cityRef)));
            final User user = new User(id, xmlUser.getValue(), xmlUser.getEmail(), UserFlag.valueOf(xmlUser.getFlag().value()), cityIdFromBase);

            if (groupRefs != null) {
                List<String> groupList = new ArrayList<>(Arrays.asList(groupRefs.split("\\s+")));
                map.putAll(id, groupList);
            }
            id++;

            chunk.add(user);
            if (chunk.size() == chunkSize) {
                addChunkFutures(chunkFutures, chunk);
                chunk = new ArrayList<>(chunkSize);
                id = userDao.getSeqAndSkip(chunkSize);
            }
        }
        agregator.setUserGroupMap(map);


        if (!chunk.isEmpty()) {
            addChunkFutures(chunkFutures, chunk);
        }

        List<FailedEmails> failed = new ArrayList<>();
        List<String> allAlreadyPresents = new ArrayList<>();
        chunkFutures.forEach((emailRange, future) -> {
            try {
                List<String> alreadyPresentsInChunk = future.get();
                log.info("{} successfully executed with already presents: {}", emailRange, alreadyPresentsInChunk);
                allAlreadyPresents.addAll(alreadyPresentsInChunk);
            } catch (InterruptedException | ExecutionException e) {
                log.error(emailRange + " failed", e);
                failed.add(new FailedEmails(emailRange, e.toString()));
            }
        });
        if (!allAlreadyPresents.isEmpty()) {
            failed.add(new FailedEmails(allAlreadyPresents.toString(), "already presents"));
        }
        return failed;
    }

    private void addChunkFutures(Map<String, Future<List<String>>> chunkFutures, List<User> chunk) {
        String emailRange = String.format("[%s-%s]", chunk.get(0).getEmail(), chunk.get(chunk.size() - 1).getEmail());
        Future<List<String>> future = executorService.submit(() -> userDao.insertAndGetConflictEmails(chunk));
        chunkFutures.put(emailRange, future);
        log.info("Submit chunk: " + emailRange);
    }
}
