package ngram;

import java.util.ArrayList;
import java.util.List;

public class NGramDescriptor {
  private final List<Integer> startIndices;
  private final int length;
  private final List<NGramDescriptor> parents;
  private int occurrencesCount;
  private int textCount;

  public NGramDescriptor(int length) {
    this.startIndices = new ArrayList<>();
    this.length = length;
    this.occurrencesCount = 0;
    this.textCount = 0;
    this.parents = new ArrayList<>();
  }

  public NGramDescriptor(int length, NGramDescriptor parent) {
    this(length);
    this.parents.add(parent);
  }

  public List<Integer> getStartIndices() {
    return this.startIndices;
  }

  public void addStartIndex(int startIndex) {
    this.startIndices.add(startIndex);
  }

  public int getLength() {
    return this.length;
  }

  public List<NGramDescriptor> getParents() {
    return this.parents;
  }

  public void addParent(NGramDescriptor parent) {
    this.parents.add(parent);
  }

  public int getOccurrencesCount() {
    return this.occurrencesCount;
  }

  public void increaseOccurrencesCount() {
    this.occurrencesCount += 1;
  }

  public int getTextCount() {
    return this.textCount;
  }

  public void increaseTextCount() {
    this.textCount += 1;
  }
}
