package team.nsu.cl.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Corpus {
  private final Map<String, Set<Occurrence>> inverseIndex;
  private final List<Document> documents;

  public Corpus() {
    this.inverseIndex = new HashMap<>();
    this.documents = new ArrayList<>();
  }

  public void addOccurrence(String word, Occurrence occurrence) {
    this.inverseIndex.computeIfAbsent(word, k -> new HashSet<>()).add(occurrence);
  }

  public void addDocument(Document document) {
    this.documents.add(document);
  }

  public Set<Occurrence> findOccurrences(String word) {
    return this.inverseIndex.get(word);
  }

  public Document getDocument(int index) {
    return this.documents.get(index - 1);
  }

  public int getDocumentsNumber() {
    return documents.size();
  }
}