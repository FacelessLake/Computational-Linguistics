package team.nsu.cl.dictionary;

import java.util.List;
import java.util.Map;

@SuppressWarnings("SpellCheckingInspection")
public interface Dictionary {
  static Dictionary load(String filename) throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }

  Grammeme getGrammeme(String name);

  List<Lemma> getLemmas(String word);
}
