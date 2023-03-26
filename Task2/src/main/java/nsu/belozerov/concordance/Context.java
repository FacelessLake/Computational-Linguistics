package nsu.belozerov.concordance;

public class Context implements Comparable<Context> {
  private final ContextHolder contextHolder;
  private final int frequency;

  public Context(ContextHolder contextHolder, int frequency) {
    this.contextHolder = contextHolder;
    this.frequency = frequency;
  }

  public int getFrequency() {
    return this.frequency;
  }

  @Override
  public int compareTo(Context anotherContext) {
    return Integer.compare(this.frequency, anotherContext.frequency);
  }

  @Override
  public String toString() {
    return this.contextHolder.toString();
  }
}
