package team.nsu.cl.model;

import java.util.List;
import java.util.Map;

@SuppressWarnings("SpellCheckingInspection")
public record Model(String word, List<Map<String, String>> surroundingWordsGrammemes, Integer id) {
}
