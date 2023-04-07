package ngram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NGram implements Comparable<NGram> {
  private final String content;
  private final int absoluteFrequency;
  private final double textFrequency;
  private final double idf;
  private final NGramType nGramType;
  private final List<NGram> parents;

  public NGram(String content, int absoluteFrequency, double textFrequency, double idf, NGramType nGramType, NGram... parents) {
    this.content = content;
    this.absoluteFrequency = absoluteFrequency;
    this.textFrequency = textFrequency;
    this.idf = idf;
    this.nGramType = nGramType;
    this.parents = Arrays.asList(parents);
  }

  public int getAbsoluteFrequency() {
    return this.absoluteFrequency;
  }

  public double getTextFrequency() {
    return this.textFrequency;
  }

  public double getIdf() {
    return this.idf;
  }

  public NGramType getNGramType() {
    return this.nGramType;
  }

  public List<NGram> subNGrams() {
    List<NGram> subNGrams = new ArrayList<>(this.parents);
    getParents(subNGrams);
    return subNGrams;
  }

  @Override
  public int compareTo(NGram anotherNGram) {
    return Integer.compare(this.absoluteFrequency, anotherNGram.absoluteFrequency);
  }

  @Override
  public String toString() {
    return switch (this.nGramType) {
      case ROOT_NGRAM -> this.content;
      case LEFT_HAND_EXPANSION, MULTI_EXPANSION -> this.content + " " + this.parents.get(0).toString();
          case RIGHT_HAND_EXPANSION -> this.parents.get(0).toString() + " " + this.content;
    };
  }

  private void getParents(List<NGram> subNGrams) {
    this.parents.forEach(parent -> subNGrams.addAll(parent.parents));
    this.parents.forEach(parent -> parent.getParents(subNGrams));
  }
}
