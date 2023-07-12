package team.nsu.cl.dictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("SpellCheckingInspection")
public class Lemma {
  private final List<Form> forms;
  private String word;
  private final Map<String, List<Grammeme>> grammemes;

  public Lemma() {
    this.forms = new ArrayList<>();
    this.grammemes = new HashMap<>();
  }

  public Lemma(List<Form> forms, String word, Map<String, List<Grammeme>> grammemes) {
    this.forms = forms;
    this.word = word;
    this.grammemes = grammemes;
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
