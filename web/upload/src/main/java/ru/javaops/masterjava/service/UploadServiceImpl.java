package ru.javaops.masterjava.service;

import org.slf4j.Logger;
import ru.javaops.masterjava.UploadService;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.User;
import ru.javaops.masterjava.upload.UserProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.*;

import static org.slf4j.LoggerFactory.getLogger;

public class UploadServiceImpl  implements UploadService {
    private static final Logger log = getLogger(UploadServiceImpl.class);
    private final UserProcessor userProcessor = new UserProcessor();


    @Override
    public List<User> getXMLData(InputStream is, int chunkNumber) {
        List<User> userList = null;
        try {
            userList = userProcessor.process(is);

        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        if (userList != null & !userList.isEmpty()) {
            saveUserList(userList, chunkNumber);
            List<User> finalUserList = userList;
            CompletableFuture.runAsync(() -> {
                saveUserList(finalUserList, chunkNumber);
                log.info("All users were save in database.");
            }).exceptionally(exception -> {
                log.error("Error save users to database: " + exception);
                return null;
            });

/*            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Future<Integer> result = executorService.submit(() -> saveUserList(finalUserList, chunkNumber));
            executorService.shutdown();
            try {
                result.get();
                log.info("All users were save in database.");
            } catch (InterruptedException e) {
                log.debug("Interrupted, closing" + e);
            } catch (ExecutionException e) {
                log.error("Error save users to database: " + e);
            }*/
        }
        return userList;
    }

    @Override
    public int saveUserList(List<User> userList, int chunkNumber) {
        UserDao dao = DBIProvider.getDao(UserDao.class);
        return dao.insertBatchChunkNumber(userList, chunkNumber);
    }
}
