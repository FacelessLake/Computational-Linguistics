package team.nsu.cl;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
import team.nsu.cl.model.Model;
import team.nsu.cl.model.TextParser;

@SuppressWarnings({"DuplicatedCode", "SpellCheckingInspection"})
public class ArticleParser {
    private static final String CHARSET = "UTF-8";
    private final XMLInputFactory streamFactory;
    private final TextParser textParser;
    private final Deque<String> tagContext;
    private final List<String> occurrences;

    public ArticleParser(FrequencyDictionaryParser frequencyDictionary, OpencorporaDictionary opencorporaDictionary, Model model) {
        this.streamFactory = XMLInputFactory.newInstance();
        this.textParser = new TextParser(frequencyDictionary, opencorporaDictionary, model);
        this.tagContext = new LinkedList<>();
        this.occurrences = new ArrayList<>();
    }

    public List<String> parse(String filename) {
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
        return this.occurrences;
    }

    private void processStartElement(StartElement startElement) {
        switch (startElement.getName().getLocalPart()) {
            case "article" -> processAttributes("article", startElement.getAttributes(), this::processArticle);
            case "doc" -> processAttributes("doc", startElement.getAttributes(), this::processDoc);
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
    }

    private void processDoc(String value) {
        this.occurrences.addAll(this.textParser.parseOneText(value));
    }
}
