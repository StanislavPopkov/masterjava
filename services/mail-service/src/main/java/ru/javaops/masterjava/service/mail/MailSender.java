package ru.javaops.masterjava.service.mail;

import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import ru.javaops.masterjava.config.Configs;
import ru.javaops.masterjava.service.model.MailResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class MailSender {
    private static Config mail;
    private static MailServiceExecutor mailServiceExecutor = new MailServiceExecutor();

    public static void initConfigMail() {
        mail = Configs.getConfig("mail.properties");
    }

    static List<MailResult> sendMail(List<Addressee> to, List<Addressee> cc, String subject, String body) {
        log.info("Send mail to \'" + to + "\' cc \'" + cc + "\' subject \'" + subject + (log.isDebugEnabled() ? "\nbody=" + body : ""));
        initConfigMail();

        if (to == null || to.isEmpty()) {
            throw new RuntimeException(("\"to\" list does not contain emails"));
        }
        Set<Email> emailSet = new HashSet<>();
        Set<Addressee> addresseeSet = new HashSet<>(to);
        if (cc != null && !cc.isEmpty()) {
            addresseeSet.addAll(cc);
        }
        addresseeSet.forEach(addressee -> {
            Email email = new SimpleEmail();
            email.setHostName(mail.getString("mail.host"));
            email.setSmtpPort(mail.getInt("mail.port"));
            email.setAuthenticator(new DefaultAuthenticator(mail.getString("mail.username"), mail.getString("mail.password")));
            email.setSSLOnConnect(mail.getBoolean("mail.useSSL"));
            email.setSubject(subject);
            try {
                email.setFrom(mail.getString("mail.username"));
                email.setMsg(body);
                email.addTo(addressee.getEmail());
                emailSet.add(email);
            } catch (EmailException e) {
                log.error(e.getMessage());
            }
        });

        return mailServiceExecutor.sendToList(emailSet);
    }
}
