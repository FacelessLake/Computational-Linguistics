package team.nsu.cl.dictionary;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

@SuppressWarnings({"SpellCheckingInspection", "DuplicatedCode"})
public class OpencorporaDictionaryParser {
  private static final String CHARSET = "UTF-8";
  private final XMLInputFactory streamFactory;
  private final OpencorporaDictionary opencorporaDictionary;
  private final Deque<String> tagContext;
  private Grammeme grammemeContext;
  private Lemma lemmaContext;
  private final Set<String> formContext;

  public OpencorporaDictionaryParser() {
    this.streamFactory = XMLInputFactory.newInstance();
    this.opencorporaDictionary = new OpencorporaDictionary();
    this.tagContext = new LinkedList<>();
    this.formContext = new HashSet<>();
  }

  public OpencorporaDictionary parse(String filename) {
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
    return this.opencorporaDictionary;
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
      case "grammeme" -> processElement(this::processGrammeme);
      case "name" -> processElement(this::processName);
      case "alias" -> processElement(this::processAlias);
      case "description" -> processElement(this::processDescription);
      case "lemma" -> processElement(this::processLemma);
      case "l" -> processElement(this::processL);
      case "f" -> processElement(this::processF);
      case "g" -> processElement(this::processG);
    }
  }

  private void processAttributes(String tag, Iterator<Attribute> iterator, Consumer<String> processor) {
    this.tagContext.push(tag);
    if (Objects.requireNonNull(this.tagContext.peek()).equals("lemma")) {
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

  private void processElement(Consumer<String> processor) {
    if (List.of("grammeme", "lemma").contains(this.tagContext.pop())) {
      processData("", processor);
    }
  }

  private void processGrammeme(String value) {
    if (!this.tagContext.isEmpty() && this.tagContext.peek().equals("grammeme")) {
      this.grammemeContext = new Grammeme();
      this.grammemeContext.setParent(value);
    } else this.grammemeContext = null;
  }

  private void processName(String value) {
    this.grammemeContext.setName(value);
    this.opencorporaDictionary.addGrammeme(this.grammemeContext);
  }

  private void processAlias(String value) {
    this.grammemeContext.setAlias(value);
  }

  private void processDescription(String value) {
    this.grammemeContext.setDescription(value);
  }

  private void processLemma(String value) {
    if (this.tagContext.isEmpty() || !this.tagContext.peek().equals("lemma")) {
      this.formContext.forEach(form -> this.opencorporaDictionary.addForm(form, this.lemmaContext));
      this.lemmaContext = null;
      this.formContext.clear();
    } else this.lemmaContext = new Lemma();
  }

  private void processL(String value) {
    this.lemmaContext.setWord(value);
    this.formContext.add(value);
  }

  private void processF(String value) {
    this.formContext.add(value);
  }

  private void processG(String value) {
    if (!this.tagContext.contains("f")) {
      this.lemmaContext.addGrammeme(this.opencorporaDictionary.getGrammeme(value));
    }
  }
}
