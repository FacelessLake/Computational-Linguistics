package org.example.lematization;

import java.util.ArrayList;
import java.util.List;

public class Lemma {
  private final List<Form> forms;
  private String word;
  private final Properties properties;

  public Lemma() {
    this.forms = new ArrayList<>();
    this.properties = new Properties();
  }

  public Lemma(List<Form> forms, String word, Properties properties) {
    this.forms = forms;
    this.word = word;
    this.properties = properties;
  }

  public List<Form> getForms() {
    return this.forms;
  }

  public void addForm(Form form) {
    this.forms.add(form);
  }

  public String getWord() {
    return this.word;
  }

  public void setWord(String word) {
    this.word = word;
  }

  public Properties getProperties() {
    return this.properties;
  }

  public void addProperty(Grammeme grammeme) {
    this.properties.addGrammeme(grammeme);
  }
}
