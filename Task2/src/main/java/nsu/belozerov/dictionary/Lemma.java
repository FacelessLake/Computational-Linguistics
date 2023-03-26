package nsu.belozerov.dictionary;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public class Lemma {
  private String word;
  private final List<Grammeme> grammemes;

  public Lemma() {
    this.grammemes = new ArrayList<>();
  }

  public Lemma(String word, List<Grammeme> grammemes) {
    this.word = word;
    this.grammemes = grammemes;
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
