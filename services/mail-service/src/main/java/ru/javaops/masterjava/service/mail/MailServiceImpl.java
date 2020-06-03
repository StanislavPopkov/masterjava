package ru.javaops.masterjava.service.mail;

import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.service.dao.MailServiceDao;
import ru.javaops.masterjava.service.model.MailResult;

import javax.jws.WebService;
import java.util.List;

@WebService(endpointInterface = "ru.javaops.masterjava.service.mail.MailService")
public class MailServiceImpl implements MailService {
    private MailServiceDao mailServiceDao = DBIProvider.getDao(MailServiceDao.class);

    public void sendMail(List<Addressee> to, List<Addressee> cc, String subject, String body) {
        List<MailResult> results = MailSender.sendMail(to, cc, subject, body);
        mailServiceDao.insertBatch(results);
    }
}