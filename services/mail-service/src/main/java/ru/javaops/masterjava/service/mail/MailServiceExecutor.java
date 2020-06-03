package ru.javaops.masterjava.service.mail;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.Email;
import ru.javaops.masterjava.service.model.MailResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
public class MailServiceExecutor {

    private final ExecutorService mailExecutor = Executors.newFixedThreadPool(8);

    public List<MailResult> sendToList(Set<Email> emails) {
        final CompletionService<MailResult> completionService = new ExecutorCompletionService<>(mailExecutor);

        List<Future<MailResult>> futures = emails.stream()
                .map(email -> completionService.submit(() -> sendToUser(email)))
                .collect(Collectors.toList());

        List<MailResult> mailResultList = new ArrayList<>();

        while (!futures.isEmpty()) {
            try {
                Future<MailResult> future = completionService.poll(10, TimeUnit.SECONDS);
                futures.remove(future);
                MailResult mailResult = future.get();
                mailResultList.add(mailResult);
            } catch (InterruptedException | ExecutionException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
        return mailResultList;
    }

    public MailResult sendToUser(Email email) throws Exception {
        String emailStr = email.getToAddresses().get(0).getAddress();
        String result = email.send();
        return new MailResult(emailStr, result);
    }

}