package ru.javaops.masterjava.persist.model;

public enum GroupTypePersist {

    REGISTERING,
    CURRENT,
    FINISHED;

    public String value() {
        return name();
    }

    public static GroupTypePersist fromValue(String v) {
        return valueOf(v);
    }
}
