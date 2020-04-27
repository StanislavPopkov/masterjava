package ru.javaops.masterjava;

import ru.javaops.masterjava.xml.MainXml;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.Schemas;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Stream;

/**
 * User: gkislin
 * Date: 05.08.2015
 *
 * @link http://caloriesmng.herokuapp.com/
 * @link https://github.com/JavaOPs/topjava
 */
public class Main {
    public static void main(String[] args) throws Exception {
        System.out.format("Hello MasterJava!");
        System.out.println();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        Stream<String> stream = in.lines().limit(1L);
        String project = stream.findFirst().orElse("null");
        JaxbParser parser = new JaxbParser(ObjectFactory.class);
        parser.setSchema(Schemas.ofClasspath("payload.xsd"));
        MainXml mainXml = new MainXml(parser);
        List<String> resultList = mainXml.userList(project);
        if (!resultList.isEmpty()) {
            System.out.println(resultList);
        } else {
            System.out.println("Not Found");
        }
    }
}
