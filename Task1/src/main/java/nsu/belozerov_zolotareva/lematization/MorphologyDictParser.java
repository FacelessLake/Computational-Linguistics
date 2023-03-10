package nsu.belozerov_zolotareva.lematization;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

@SuppressWarnings("SpellCheckingInspection")
public class MorphologyDictParser {
  private static final String CHARSET = "UTF-8";
  private final XMLInputFactory streamFactory;
  private final MorphologyDict morphologyDict;
  private final Deque<String> tagContext;
  private Grammeme grammemeContext;
  private Lemma lemmaContext;
  private Form formContext;

  public MorphologyDictParser() {
    this.streamFactory = XMLInputFactory.newInstance();
    this.morphologyDict = new MorphologyDict();
    this.tagContext = new LinkedList<>();
  }

  public MorphologyDict parse(String filename) {
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
    return this.morphologyDict;
  }

  private void processStartElement(StartElement startElement) {
    switch (startElement.getName().getLocalPart()) {
      case "grammeme" -> processAttributes("grammeme", startElement.getAttributes(), this::processGrammeme);
      case "name" -> processAttributes("name", startElement.getAttributes(), this::processName);
      case "alias" -> processAttributes("alias", startElement.getAttributes(), this::processAlias);
      case "description" -> processAttributes("description", startElement.getAttributes(), this::processDescription);
      case "lemma" -> processAttributes("lemma", startElement.getAttributes(), this::processLemma);
      case "l" -> processAttributes("l", startElement.getAttributes(), this::processL);
      case "f" -> processAttributes("f", startElement.getAttributes(), this::processF);
      case "g" -> processAttributes("g", startElement.getAttributes(), this::processG);
    }
  }

  private void processCharacters(Characters characters) {
    if (!this.tagContext.isEmpty()) {
      switch (this.tagContext.peek()) {
        case "name" -> processData(characters.getData().trim().replaceAll(" +", " "), this::processName);
        case "alias" -> processData(characters.getData().trim().replaceAll(" +", " "), this::processAlias);
        case "description" -> processData(characters.getData().trim().replaceAll(" +", " "), this::processDescription);
      }
    }
  }

  private void processEndElement(EndElement endElement) {
    switch (endElement.getName().getLocalPart()) {
      case "grammeme" -> this.grammemeContext = null;
      case "lemma" -> this.lemmaContext = null;
      case "f" -> this.formContext = null;
    }
    if (List.of("grammeme", "name", "alias", "description", "lemma", "l", "f", "g").contains(endElement.getName().getLocalPart())) {
      this.tagContext.pop();
    }
  }

  private void processAttributes(String tag, Iterator<Attribute> iterator, Consumer<String> processor) {
    this.tagContext.push(tag);
    if (tag.equals("lemma")) {
      processData("", processor);
    }
    while (iterator.hasNext()) {
      Attribute attribute = iterator.next();
      String localPart = attribute.getName().getLocalPart();
      String value = attribute.getValue().trim().replaceAll(" +", " ");
      if (List.of("parent", "t", "v").contains(localPart)) {
        processData(value, processor);
      }
    }
  }

  private void processData(String value, Consumer<String> processor) {
    processor.accept(value);
  }

  private void processGrammeme(String value) {
    this.grammemeContext = new Grammeme();
    this.grammemeContext.setParent(value);
  }

  private void processName(String value) {
    this.grammemeContext.setName(value);
    this.morphologyDict.addGrammeme(this.grammemeContext);
  }

  private void processAlias(String value) {
    this.grammemeContext.setAlias(value);
  }

  private void processDescription(String value) {
    this.grammemeContext.setDescription(value);
  }

  private void processLemma(String value) {
    this.lemmaContext = new Lemma();
  }

  private void processL(String value) {
    this.lemmaContext.setWord(value);
  }

  private void processF(String value) {
    this.formContext = new Form();
    this.formContext.setLemma(this.lemmaContext);
    this.formContext.setWord(value);
    this.lemmaContext.addForm(this.formContext);
    this.morphologyDict.addForm(this.formContext);
  }

  private void processG(String value) {
    Grammeme grammeme = this.morphologyDict.getGrammeme(value);
    if (Objects.nonNull(this.formContext)) {
      this.formContext.addGrammeme(grammeme);
    } else if (Objects.nonNull(this.lemmaContext)) {
      this.lemmaContext.addGrammeme(grammeme);
    }
  }
}
