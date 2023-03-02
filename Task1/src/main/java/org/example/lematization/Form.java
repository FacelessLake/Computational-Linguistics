package org.example.lematization;

public class Form {
  private Lemma lemma;
  private String word;
  private final Properties properties;

  public Form() {
    this.properties = new Properties();
  }

  public Form(Lemma lemma, String word, Properties properties) {
    this.lemma = lemma;
    this.word = word;
    this.properties = properties;
  }

  public Lemma getLemma() {
    return this.lemma;
  }

  public void setLemma(Lemma lemma) {
    this.lemma = lemma;
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
