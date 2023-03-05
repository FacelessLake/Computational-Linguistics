package nsu.belozerov_zolotareva;

import nsu.belozerov_zolotareva.dictionary_builder.TextParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;

public class ArticlesParser extends DefaultHandler {

    private final TextParser textParser;

    public ArticlesParser() {
        textParser = new TextParser();
    }

    @Override
    public void endDocument() {
        try {
            textParser.printAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equals("doc")) {
            textParser.increaseDocCounter();
        }
//        System.out.println(attributes.getValue("id"));
    }



    @Override
    public void characters(char[] ch, int start, int length) {
        String value = (new String(ch, start, length));
        textParser.parseOneText(value);
    }
}
