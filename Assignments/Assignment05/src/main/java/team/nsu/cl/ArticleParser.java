package team.nsu.cl;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import team.nsu.cl.dictionary.OpencorporaDictionary;
import team.nsu.cl.dictionary.frequency.FrequencyDictionaryParser;
import team.nsu.cl.index.Corpus;
import team.nsu.cl.index.TextParser;

@SuppressWarnings("DuplicatedCode")
public class ArticleParser {
    private static final String CHARSET = "UTF-8";
    private final XMLInputFactory streamFactory;
    private final TextParser textParser;
    private final Deque<String> tagContext;
    private Corpus corpus;

    public ArticleParser(FrequencyDictionaryParser frequencyDictionaryParser, OpencorporaDictionary dictionary) {
        this.streamFactory = XMLInputFactory.newInstance();
        this.textParser = new TextParser(frequencyDictionaryParser, dictionary);
        this.tagContext = new LinkedList<>();
    }

    public Corpus parse(String filename) {
        try {
            XMLEventReader reader = this.streamFactory.createXMLEventReader(new BufferedInputStream(new FileInputStream(filename)), CHARSET);
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                if (event.isStartElement()) {
                    processStartElement(event.asStartElement());
                } else if (event.isCharacters()) {
                    processCharacters(event.asCharacters());
                } else if (event.isEndElement()) {
                    processEndElement(event.asEndElement());
                }
            }
        } catch (FileNotFoundException | XMLStreamException e) {
            e.printStackTrace();
        }
        return this.corpus;
    }

    private void processStartElement(StartElement startElement) {
        switch (startElement.getName().getLocalPart()) {
            case "article" -> processAttributes("article", startElement.getAttributes(), this::processArticle);
            case "doc" -> processAttributes("doc", startElement.getAttributes(), this::processTitle);
        }
    }

    private void processCharacters(Characters characters) {
        if (!this.tagContext.isEmpty()) {
            if (this.tagContext.peek().equals("doc")) {
                processData(characters.getData().trim().replaceAll(" +", " "), this::processDoc);
            }
        }
    }

    private void processEndElement(EndElement endElement) {
        switch (endElement.getName().getLocalPart()) {
            case "article" -> processElement(this::processArticle);
            case "doc" -> processElement(this::processDoc);
        }
    }

    private void processAttributes(String tag, Iterator<Attribute> attributes, Consumer<String> processor) {
        this.tagContext.push(tag);
        while (attributes.hasNext()) {
            Attribute attribute = attributes.next();
            String localPart = attribute.getName().getLocalPart();
            String value = attribute.getValue().trim().replaceAll(" +", " ");
            if (localPart.equals("title")) {
                processData(value, processor);
            }
        }
    }

    private void processData(String value, Consumer<String> processor) {
        processor.accept(value);
    }

    private void processElement(Consumer<String> processor) {
        if (this.tagContext.pop().equals("article")) {
            processData("", processor);
        }
    }

    private void processArticle(String value) {
        this.corpus = this.textParser.buildIndices();
    }

    private void processDoc(String value) {
        this.textParser.parseOneText(value);
    }

    private void processTitle(String value) {
        this.textParser.increaseDocCount(value);
    }
}
