package ru.javaops.masterjava.xml.util;

import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import java.io.*;


/**
 * Marshalling/Unmarshalling JAXB helper
 * XML Facade
 */
public class JaxbParser {

    ThreadLocal<JaxbMarshaller> threadLocalM;
    ThreadLocal<JaxbUnmarshaller> threadLocalU;
    protected Schema schema;

    public JaxbParser(Class... classesToBeBound) {
        try {
            init(JAXBContext.newInstance(classesToBeBound));
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    //    http://stackoverflow.com/questions/30643802/what-is-jaxbcontext-newinstancestring-contextpath
    public JaxbParser(String context) {
        try {
            init(JAXBContext.newInstance(context));
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void init(JAXBContext ctx) throws JAXBException {
        threadLocalM = new ThreadLocal<>();
        threadLocalM.set(new JaxbMarshaller(ctx));
        threadLocalU = new ThreadLocal<>();
        threadLocalU.set(new JaxbUnmarshaller(ctx));
    }

    // Unmarshaller
    public <T> T unmarshal(InputStream is) throws JAXBException {
        return (T) threadLocalU.get().unmarshal(is);
    }

    public <T> T unmarshal(Reader reader) throws JAXBException {
        return (T) threadLocalU.get().unmarshal(reader);
    }

    public <T> T unmarshal(String str) throws JAXBException {
        return (T) threadLocalU.get().unmarshal(str);
    }

    public <T> T unmarshal(XMLStreamReader reader, Class<T> elementClass) throws JAXBException {
        return threadLocalU.get().unmarshal(reader, elementClass);
    }

    // Marshaller
    public void setMarshallerProperty(String prop, Object value) {
        try {
            threadLocalM.get().setProperty(prop, value);
        } catch (PropertyException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String marshal(Object instance) throws JAXBException {
        return threadLocalM.get().marshal(instance);
    }

    public void marshal(Object instance, Writer writer) throws JAXBException {
        threadLocalM.get().marshal(instance, writer);
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
        threadLocalU.get().setSchema(schema);
        threadLocalM.get().setSchema(schema);
    }

    public void validate(String str) throws IOException, SAXException {
        validate(new StringReader(str));
    }

    public void validate(Reader reader) throws IOException, SAXException {
        schema.newValidator().validate(new StreamSource(reader));
    }
}
