package ru.javaops.masterjava.service.model;

import ru.javaops.masterjava.persist.model.BaseEntity;

public class MailResult extends BaseEntity {
    private String email;
    private String result;


    public MailResult(Integer id, String email, String result) {
        super(id);
        this.email = email;
        this.result = result;
    }

    public MailResult(String email, String result) {
        this.email = email;
        this.result = result;
    }

    public MailResult() {
    }

    public String getEmail() {
        return email;
    }

    public String getResult() {
        return result;
    }

    @Override
    public String toString() {
        return '(' + email + ',' + result + ')';
    }
}