package ngram;

public class NGramHolder {
  private final StringBuilder text;
  private final int startIndex;
  private final int endIndex;

  public NGramHolder(StringBuilder text, int startIndex, int endIndex) {
    this.text = text;
    this.startIndex = startIndex;
    this.endIndex = endIndex;
  }

  @Override
  public String toString() {
    return this.text.substring(this.startIndex, this.endIndex);
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == this) {
      return true;
    }
    if(!(obj instanceof NGramHolder)) {
      return false;
    }
    return toString().equals(obj.toString());
  }

  @Override
  public int hashCode() {
    return toString().hashCode();
  }
}
