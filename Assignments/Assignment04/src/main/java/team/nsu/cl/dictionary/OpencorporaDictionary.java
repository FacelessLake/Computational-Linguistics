package team.nsu.cl.dictionary;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("SpellCheckingInspection")
public class OpencorporaDictionary implements Dictionary {
  private final Map<String, Grammeme> grammemes;
  private final Map<String, Set<Form>> forms;

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

  public List<Form> getForms(String word) {
    if (Objects.isNull(this.forms.get(word))) {
      return null;
    }
    return List.copyOf(this.forms.get(word));
  }

  public void addForm(Form form) {
    this.forms.computeIfAbsent(form.getWord(), k -> new HashSet<>()).add(form);
  }

  @Override
  public List<Lemma> getLemmas(String word) {
    if (Objects.isNull(this.forms.get(word))) {
      return null;
    }
    return this.forms.get(word).stream().map(Form::getLemma).distinct().toList();
  }
}
