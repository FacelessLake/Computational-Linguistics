package org.example.lematization;

import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("SpellCheckingInspection")
public interface Dictionary {
  static Dictionary load(String filename) throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }

  Grammeme getGrammeme(String name);

  Map<String, List<Form>> getForms(String word);

  Set<Lemma> getLemmas(String word);
}
