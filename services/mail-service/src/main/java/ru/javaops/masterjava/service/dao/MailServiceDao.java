package ru.javaops.masterjava.service.dao;

import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import ru.javaops.masterjava.persist.dao.AbstractDao;
import ru.javaops.masterjava.service.model.MailResult;

import java.util.List;

public abstract class MailServiceDao implements AbstractDao {

    @SqlBatch("INSERT INTO mail_result (email, result) VALUES (:email, :result)")
    public abstract void insertBatch(@BindBean List<MailResult> results);
}