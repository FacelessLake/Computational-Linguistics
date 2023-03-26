package nsu.belozerov;

import nsu.belozerov.concordance.*;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("DuplicatedCode")
public class ArticleParser {
    private static final String CHARSET = "UTF-8";
    private final XMLInputFactory streamFactory;
    private final TextParser textParser;
    private final Deque<String> tagContext;
    private final Writer outputWriter;

    public ArticleParser(String substring, int length) throws FileNotFoundException {
        this.streamFactory = XMLInputFactory.newInstance();
        this.textParser = new TextParser(substring, length);
        this.tagContext = new LinkedList<>();
        outputWriter = new OutputStreamWriter(new FileOutputStream("output.txt"), StandardCharsets.UTF_8);
    }

    public void parse(String filename) {
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
                } else if (event.isEndDocument()) {
                    printContext(textParser.getLeftContexts(), "left");
                    printContext(textParser.getRightContexts(), "right");
                    printContext(textParser.getLeftRightContexts(), "left-right");
                }
            }
        } catch (FileNotFoundException | XMLStreamException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public void processAttributes(String tag, Iterator<Attribute> attributes, Consumer<String> processor) {
        this.tagContext.push(tag);
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
    }

    public void processDoc(String value) {
        this.textParser.parseOneText(value);
    }

    public void printContext(List<Context> contextList, String type) throws IOException {
        ArrayList<Context> result = new ArrayList<>(contextList);
        result.sort((o1, o2) -> -o1.compareTo(o2));

        for (Context res : result) {
            int freq = res.getFrequency();
            if (freq < 2) {
                break;
            }
            StringBuilder str = new StringBuilder();
            str.append("<");
            str.append(type);
            str.append(", ");
            str.append(res.toString().trim());
            str.append(", ");
            str.append(freq);
            str.append(">\n");
            outputWriter.write(str.toString());
        }
        outputWriter.flush();
    }
}
