package org.example.lematization;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public class Properties {
  private final List<Grammeme> grammemes;

  public Properties() {
    this.grammemes = new ArrayList<>();
  }

  public Properties(List<Grammeme> grammemes) {
    this.grammemes = grammemes;
  }

  public List<Grammeme> getGrammemes() {
    return this.grammemes;
  }

  public void addGrammeme(Grammeme grammeme) {
    this.grammemes.add(grammeme);
  }
}
