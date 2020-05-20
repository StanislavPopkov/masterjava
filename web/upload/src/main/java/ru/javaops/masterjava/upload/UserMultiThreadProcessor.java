package ru.javaops.masterjava.upload;

import org.slf4j.Logger;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.slf4j.LoggerFactory.getLogger;

public class UserMultiThreadProcessor {
    private static final Logger log = getLogger(UserMultiThreadProcessor.class);
    private ExecutorService executorService;


    public List<User> multisaveUsers(List<User> userList, int chunkNumber) {
        executorService =  Executors.newFixedThreadPool(4);
        int j = 0;
        List<Future> futureList = new ArrayList<>();
        List<User> userListToThread = new ArrayList<>();
        List<User> userListForReturn = new ArrayList<>();
        for (User user : userList) {
            userListToThread.add(user);
            j++;
            if (j == chunkNumber) {
                List<User> finalUserListToThread = userListToThread;
                futureList.add(executorService.submit(() -> saveUserList(finalUserListToThread, chunkNumber)));
                userListToThread = new ArrayList<>();
                j = 0;
            }
        }
        if (userListToThread != null && !userListToThread.isEmpty()) {
            List<User> finalUserListToThread = userListToThread;
            futureList.add(executorService.submit(() -> saveUserList(finalUserListToThread, chunkNumber)));
        }
        executorService.shutdown();
        try {
            for (Future future : futureList) {
                userListForReturn.addAll((List)future.get());
            }
            log.info("Users were save in without exception.");
        } catch (InterruptedException e) {
            log.debug("Interrupted, closing" + e);
        } catch (ExecutionException e) {
            log.error("Error save users to database: " + e);
        }
        return userListForReturn;
    }

    public List<User> saveUserList(List<User> userList, int chunkNumber) {
        UserDao dao = DBIProvider.getDao(UserDao.class);
        int [] result = dao.insertBatchChunkNumber(userList, chunkNumber);
        return IntStream.range(0, userList.size()).filter(index -> result[index] == 0)
                .mapToObj(userList::get).collect(Collectors.toList());
    }
}
