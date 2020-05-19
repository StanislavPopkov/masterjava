package ru.javaops.masterjava;

import ru.javaops.masterjava.persist.model.User;

import java.io.InputStream;
import java.util.List;

public interface UploadService {

    List<User> getXMLData(InputStream is, int chunkNumber);

    int saveUserList(List<User> userList, int chunkNumber);
}
