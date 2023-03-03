package nsu.belozerov_zolotareva.lematization;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public class Form {
  private Lemma lemma;
  private String word;
  private final List<Grammeme> grammemes;

  public Form() {
    this.grammemes = new ArrayList<>();
  }

  public Form(Lemma lemma, String word, List<Grammeme> grammemes) {
    this.lemma = lemma;
    this.word = word;
    this.grammemes = grammemes;
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

  public List<Grammeme> getGrammemes() {
    return this.grammemes;
  }

  public void addGrammeme(Grammeme grammeme) {
    this.grammemes.add(grammeme);
  }
}
