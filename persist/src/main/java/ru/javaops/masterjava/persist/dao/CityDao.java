package ru.javaops.masterjava.persist.dao;

import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.model.City;

import java.util.List;

public abstract class CityDao implements AbstractDao {

    @SqlUpdate("TRUNCATE cities CASCADE")
    @Override
    public abstract void clean();


    public City insert(City city) {
        if (city.isNew()) {
            int id = insertGeneratedId(city);
            city.setId(id);
        } else {
            insertWitId(city);
        }
        return city;
    }

    @SqlQuery("SELECT nextval('user_seq')")
    abstract int getNextVal();

    @Transaction
    public int getSeqAndSkip(int step) {
        int id = getNextVal();
        DBIProvider.getDBI().useHandle(h -> h.execute("ALTER SEQUENCE user_seq RESTART WITH " + (id + step)));
        return id;
    }

    @SqlUpdate("INSERT INTO cities (short_name, city_name) VALUES (:shortName, :cityName) ON CONFLICT DO NOTHING")
    @GetGeneratedKeys
    abstract int insertGeneratedId(@BindBean City city);

    @SqlUpdate("INSERT INTO cities (id, short_name, city_name) VALUES (:id, :shortName, :cityName) ON CONFLICT DO NOTHING ")
    abstract void insertWitId(@BindBean City city);


    @SqlBatch("INSERT INTO cities (short_name, city_name) VALUES (:shortName, :cityName) ON CONFLICT DO NOTHING")
    @BatchChunkSize(20)
    public abstract int[] insertBatch(@BindBean List<City> ciyList);
}
