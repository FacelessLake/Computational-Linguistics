package nsu.belozerov.concordance;

public class ContextHolder {
  private final StringBuilder substring;
  private final String prefix;
  private final String suffix;

  public ContextHolder(StringBuilder substring, String prefix, String suffix) {
    this.substring = substring;
    this.prefix = prefix;
    this.suffix = suffix;
  }

  @Override
  public String toString() {
    return this.prefix + this.substring.toString() + this.suffix;
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == this) {
      return true;
    }
    if(!(obj instanceof ContextHolder)) {
      return false;
    }
    return toString().equals(obj.toString());
  }

  @Override
  public int hashCode() {
    return toString().hashCode();
  }
}
