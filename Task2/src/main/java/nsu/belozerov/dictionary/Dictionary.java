package nsu.belozerov.dictionary;

import java.util.List;

public interface Dictionary {
  static Dictionary load(String filename) throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }

  Grammeme getGrammeme(String name);

  List<Lemma> getLemmas(String word);
}
