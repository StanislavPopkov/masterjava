package ru.javaops.masterjava.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;

import java.util.Objects;

public class City extends BaseEntity {

    @Column("short_name")
    private String shortName;
    @Column("city_name")
    private String cityName;

    public City(Integer id, String shortName, String cityName) {
        super(id);
        this.shortName = shortName;
        this.cityName = cityName;
    }

    public City(String shortName, String cityName) {
        this(null, shortName, cityName);
    }

    public City() {

    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        City city = (City) o;
        return shortName.equals(city.shortName) &&
                cityName.equals(city.cityName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), shortName, cityName);
    }
}
