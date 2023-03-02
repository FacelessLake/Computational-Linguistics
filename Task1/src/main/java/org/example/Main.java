package org.example;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser SAXparser = factory.newSAXParser();
        ArticlesParser parser = new ArticlesParser();

        SAXparser.parse(new File("src/main/resources/test.xml"), parser);

//        FreqDictParser parser = new FreqDictParser("src/main/resources/frequency_dict.txt");
//        parser.parse();
//        System.out.println(parser.getData("а"));
//        System.out.println(parser.getData("печь"));
//        System.out.println(parser.getData("данных"));
    }
}