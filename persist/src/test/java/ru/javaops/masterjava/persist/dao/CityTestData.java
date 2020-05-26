package ru.javaops.masterjava.persist.dao;

import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.model.City;

public class CityTestData {
    public static City CITY;

    public static void init() {
        CITY = new City("NYC", "New-York");
    }

    public static void setUp() {
        CityDao cityDao = DBIProvider.getDao(CityDao.class);
        cityDao.clean();
        cityDao.insert(CITY);
    }
}
