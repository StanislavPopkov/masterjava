package ru.javaops.masterjava.service;

import ru.javaops.masterjava.processors.UserProcessor;

import java.util.List;

public interface UploadService {

    List<UserProcessor.FailedEmails> process(byte[] xmlArray, int chunkSize);
}
