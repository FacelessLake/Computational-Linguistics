import ngram.NGram;
import ngram.TextParser;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

@SuppressWarnings("DuplicatedCode")
public class ArticleParser {
    private static final String CHARSET = "UTF-8";
    private final XMLInputFactory streamFactory;
    private final TextParser textParser;
    private final Deque<String> tagContext;
    private List<NGram> nGrams;

    public ArticleParser(String frequencyDictionary, String opencorporaDictionary, double threshold){
        this.streamFactory = XMLInputFactory.newInstance();
        this.textParser = new TextParser(frequencyDictionary, opencorporaDictionary,threshold);
        this.tagContext = new LinkedList<>();
    }

    public List<NGram> parse(String filename) {
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
        return this.nGrams;
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

    @SuppressWarnings("ConstantConditions")
    public void processAttributes(String tag, Iterator<Attribute> attributes, Consumer<String> processor) {
        this.tagContext.push(tag);
    if (this.tagContext.peek().equals("doc")) {
        this.textParser.increaseDocCount();
    }
    }

    public void processData(String value, Consumer<String> processor) {
        processor.accept(value);
    }

    public void processElement(Consumer<String> processor) {
        if (this.tagContext.pop().equals("article")) {
            processData("", processor);
        }
    }

    public void processArticle(String value) {
        this.nGrams = this.textParser.extract();
    }

    public void processDoc(String value) {
        this.textParser.parseOneText(value);
    }
}
