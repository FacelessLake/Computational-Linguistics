package NGramDictionary;

import java.util.ArrayList;
import java.util.List;

public class DictionaryNGram {

    private DictionaryNGram parent1;
    private DictionaryNGram parent2;
    private final String parent1Content;
    private final String parent2Content;

    private final String content;
    private final String[] contentSplit;
    private final int absoluteFrequency;
    private final double textFrequency;
    private final double idf;
    private final int contentLength;
    private final List<DictionaryNGram> children;


    public DictionaryNGram(String content, int absoluteFrequency, double textFrequency, double idf) {
        this.content = content;
        this.absoluteFrequency = absoluteFrequency;
        this.textFrequency = textFrequency;
        this.idf = idf;
        children = new ArrayList<>();
        contentSplit = content.split("\\s");
        contentLength = contentSplit.length;

        String res1 = "";
        String res2 = "";
        for (int i = 0; i < contentSplit.length - 1; i++) {
            res1 = res1 + " " + contentSplit[i];
            res2 = res2 + " " + contentSplit[i + 1];
        }
        parent1Content = res1.trim();
        parent2Content = res2.trim();
    }

    public DictionaryNGram getParent1() {
        return parent1;
    }

    public void setParent1(DictionaryNGram parent1) {
        this.parent1 = parent1;
    }

    public DictionaryNGram getParent2() {
        return parent2;
    }

    public void setParent2(DictionaryNGram parent2) {
        this.parent2 = parent2;
    }

    public String getParent1Content() {
        return parent1Content;
    }

    public String getParent2Content() {
        return parent2Content;
    }

    public void addChild(DictionaryNGram child) {
        if (!children.contains(child)) {
            children.add(child);
        }
    }

    public List<DictionaryNGram> getChildren() {
        return children;
    }

    public int getAbsoluteFrequency() {
        return absoluteFrequency;
    }

    public double getTextFrequency() {
        return textFrequency;
    }

    public double getIdf() {
        return idf;
    }

    public String getContent() {
        return content;
    }

    public int getContentLength() {
        return contentLength;
    }

    public String[] getContentSplit() {
        return contentSplit;
    }

    public boolean contains(String word) {
        for (String contentWord : contentSplit) {
            if (contentWord.equals(word)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "<" + content +
                ", " + absoluteFrequency +
                ", " + textFrequency +
                ", " + idf +
                '>';
    }

    public boolean equals(DictionaryNGram dictionaryNGram) {
        return content.equals(dictionaryNGram.getContent());
    }
}