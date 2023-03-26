package nsu.belozerov.dictionary;

import java.util.*;

@SuppressWarnings("SpellCheckingInspection")
public class OpencorporaDictionary implements Dictionary {
  private final Map<String, Grammeme> grammemes;
  private final Map<String, Set<Lemma>> forms;

  public OpencorporaDictionary() {
    this.grammemes = new HashMap<>();
    this.forms = new HashMap<>();
  }

  public static OpencorporaDictionary load(String filename) {
    OpencorporaDictionaryParser opencorporaDictionaryParser = new OpencorporaDictionaryParser();
    return opencorporaDictionaryParser.parse(filename);
  }

  @Override
  public Grammeme getGrammeme(String name) {
    return this.grammemes.get(name);
  }

  public void addGrammeme(Grammeme grammeme) {
    this.grammemes.put(grammeme.getName(), grammeme);
  }

  public void addForm(String form, Lemma lemma) {
    this.forms.computeIfAbsent(form, k -> new HashSet<>()).add(lemma);
  }

  @Override
  public List<Lemma> getLemmas(String word) {
    if (!this.forms.containsKey(word)) {
      return null;
    }
    return List.copyOf(this.forms.get(word));
  }
}
