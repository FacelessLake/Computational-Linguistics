package team.nsu.cl.dictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("SpellCheckingInspection")
public class Form {
  private Lemma lemma;
  private String word;
  private final Map<String, List<Grammeme>> grammemes;

  public Form() {
    this.grammemes = new HashMap<>();
  }

  public Form(Lemma lemma, String word, Map<String, List<Grammeme>> grammemes) {
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

  public Map<String, List<Grammeme>> getGrammemes() {
    return this.grammemes;
  }

  public List<Grammeme> getGrammemes(String parent) {
    return this.grammemes.get(parent);
  }

  public void addGrammeme(Grammeme grammeme) {
    this.grammemes.computeIfAbsent(grammeme.getParent(), k -> new ArrayList<>()).add(grammeme);
  }
}
