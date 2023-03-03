package nsu.belozerov_zolotareva.lematization;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public class Lemma {
  private final List<Form> forms;
  private String word;
  private final List<Grammeme> grammemes;

  public Lemma() {
    this.forms = new ArrayList<>();
    this.grammemes = new ArrayList<>();
  }

  public Lemma(List<Form> forms, String word, List<Grammeme> grammemes) {
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

  public List<Grammeme> getGrammemes() {
    return this.grammemes;
  }

  public void addGrammeme(Grammeme grammeme) {
    this.grammemes.add(grammeme);
  }
}
