package ru.javaops.masterjava.processors;

import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.CityDao;
import ru.javaops.masterjava.persist.model.City;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class CityProcessor {
    //private static final Logger log = getLogger(CityProcessor.class);


    public List<City> process(byte[] bytes) throws XMLStreamException {

        List<City> cities = new ArrayList<>();
            try (StaxStreamProcessor processor =
                         new StaxStreamProcessor(new ByteArrayInputStream(bytes))) {
                XMLStreamReader reader = processor.getReader();
                while (reader.hasNext()) {
                    int event = reader.next();
                    if (event == XMLEvent.START_ELEMENT && "City".equals(reader.getLocalName())) {
                        City city = new City();
                        city.setShortName(reader.getAttributeValue("", "id"));
                        city.setCityName(reader.getElementText());
                        cities.add(city);
                    }
                }
            }
            return saveCities(cities);
    }

    private List<City> saveCities(List<City> cities) {
        CityDao dao = DBIProvider.getDao(CityDao.class);
        List<City> citiesWithId = new ArrayList<>();
        cities.forEach(city -> citiesWithId.add(dao.insert(city)));
        return citiesWithId;
    }

}
