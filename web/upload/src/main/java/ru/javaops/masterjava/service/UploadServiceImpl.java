package ru.javaops.masterjava.service;

import org.slf4j.Logger;
import ru.javaops.masterjava.UploadService;
import ru.javaops.masterjava.persist.model.User;
import ru.javaops.masterjava.upload.UserMultiThreadProcessor;
import ru.javaops.masterjava.upload.UserParser;

import java.io.InputStream;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class UploadServiceImpl  implements UploadService {
    private static final Logger log = getLogger(UploadServiceImpl.class);
    private final UserParser userParser = new UserParser();
    private final UserMultiThreadProcessor userMultiThreadProcessor = new UserMultiThreadProcessor();


    @Override
    public List<User> getXMLData(InputStream is, int chunkNumber) {
        List<User> userList = null;
        try {
            userList = userParser.process(is);

        } catch (Exception e) {
            log.error("Error get users from xml" + e);
        }
        if (userList != null & !userList.isEmpty()) {
            userList = userMultiThreadProcessor.multisaveUsers(userList, chunkNumber);
/*            CompletableFuture.runAsync(() -> {
                saveUserList(finalUserList, chunkNumber);
                log.info("All users were save in database.");
            }).exceptionally(exception -> {
                log.error("Error save users to database: " + exception);
                return null;
            });*/
        }
        return userList;
    }
}
