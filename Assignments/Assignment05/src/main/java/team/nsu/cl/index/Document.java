package team.nsu.cl.index;

import java.util.ArrayList;
import java.util.List;

public class Document {
  private final String title;
  private final List<String> sentences;

  public Document(String title) {
    this.title = title;
    this.sentences = new ArrayList<>();
  }

  public void addSentence(String sentence) {
    this.sentences.add(sentence);
  }

  public String getSentence(int index) {
    return this.sentences.get(index - 1);
  }

  public String getTitle() {
    return title;
  }

  public int getSentencesAmount() {
    return sentences.size();
  }
}