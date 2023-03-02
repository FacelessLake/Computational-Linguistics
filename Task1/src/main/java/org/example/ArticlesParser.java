package org.example;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;

public class ArticlesParser extends DefaultHandler {

    private final TextParser textParser;

    public ArticlesParser() {
        textParser = new TextParser();
    }

    @Override
    public void endDocument() throws SAXException {
        try {
            textParser.printAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        String value = (new String(ch, start, length));
        textParser.parseOneText(value);
    }
}
