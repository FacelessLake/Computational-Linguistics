package nsu.belozerov.concordance;

import nsu.belozerov.dictionary.*;
import nsu.belozerov.dictionary.frequency.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("SpellCheckingInspection")
public class TextParser {
    private static final String FREQUENCY_DICTIONARY = "src/main/resources/frequency_dict.txt";
    private static final String OPENCORPORA_DICTIONARY = "src/main/resources/dict.opcorpora.xml";
    private final FrequencyDictionaryParser frequencyDictionaryParser;
    private final Dictionary dictionary;
    private final Map<ContextHolder, Integer> leftContexts;
    private final Map<ContextHolder, Integer> rightContexts;
    private final Map<ContextHolder, Integer> leftRightContexts;
    private final StringBuilder substring;
    private final int length;

    public TextParser(String substring, int length) {
        this.frequencyDictionaryParser = new FrequencyDictionaryParser();
        this.frequencyDictionaryParser.parse(FREQUENCY_DICTIONARY);
        this.dictionary = OpencorporaDictionary.load(OPENCORPORA_DICTIONARY);
        this.leftContexts = new HashMap<>();
        this.rightContexts = new HashMap<>();
        this.leftRightContexts = new HashMap<>();
        this.substring = lemmatize(substring);
        this.length = length;
    }

    public void parseOneText(String text) {
        StringBuilder lemmatizedText = lemmatize(text);
        for (int i = lemmatizedText.indexOf(this.substring.toString()); i != -1; i = lemmatizedText.indexOf(this.substring.toString(), i + 1)) {
            addLeftContexts(lemmatizedText, i);
            addRightContexts(lemmatizedText, i);
            addLeftRightContexts(lemmatizedText, i);
        }
    }

    public List<Context> getLeftContexts() {
        return this.leftContexts.entrySet().stream().map(leftContext -> new Context(leftContext.getKey(), leftContext.getValue())).toList();
    }

    public List<Context> getRightContexts() {
        return this.rightContexts.entrySet().stream().map(rightContext -> new Context(rightContext.getKey(), rightContext.getValue())).toList();
    }

    public List<Context> getLeftRightContexts() {
        return this.leftRightContexts.entrySet().stream().map(leftRightContext -> new Context(leftRightContext.getKey(), leftRightContext.getValue())).toList();
    }

    private StringBuilder lemmatize(String text) {
        StringBuilder lemmas = new StringBuilder();
        String clearText = preprocess(text);
        Pattern pattern = Pattern.compile("\\p{InCYRILLIC}+(-\\p{InCYRILLIC}+)?");
        Matcher matcher = pattern.matcher(clearText);
        while (matcher.find()) {
            lemmas.append(" ");
            String word = clearText.substring(matcher.start(), matcher.end());
            List<Lemma> possibleLemmas = this.dictionary.getLemmas(word);
            lemmas.append(Objects.nonNull(possibleLemmas) ? resolveAmbiguity(possibleLemmas) : word);
        }
        lemmas.append(" ");
        return lemmas;
    }

    private void addLeftContexts(StringBuilder lemmatizedText, int index) {
        for (int i = 1; i <= this.length; i++) {
            String prefix = getPreviousWords(lemmatizedText, index, i);
            if (Objects.nonNull(prefix)) {
                this.leftContexts.merge(new ContextHolder(this.substring, prefix, ""), 1, Integer::sum);
            }
        }
    }

    private void addRightContexts(StringBuilder lemmatizedText, int index) {
        for (int i = 1; i <= this.length; i++) {
            String suffix = getNextWords(lemmatizedText, index + this.substring.length(), i);
            if (Objects.nonNull(suffix)) {
                this.rightContexts.merge(new ContextHolder(this.substring, "", suffix), 1, Integer::sum);
            }
        }
    }

    private void addLeftRightContexts(StringBuilder lemmatizedText, int index) {
        for (int i = 2; i <= this.length; i++) {
            for (int j = 1; j < i; j++) {
                String prefix = getPreviousWords(lemmatizedText, index, j);
                String suffix = getNextWords(lemmatizedText, index + this.substring.length(), i - j);
                if (Objects.nonNull(prefix) && Objects.nonNull(suffix)) {
                    this.leftRightContexts.merge(new ContextHolder(this.substring, prefix, suffix), 1, Integer::sum);
                }
            }
        }
    }

    private String preprocess(String text) {
        return text.replaceAll("\\u0301", "").replaceAll("[\\u00C1\\u00E1]", "\u0430").replaceAll("[\\u00C9\\u00E9]", "\u0435").replaceAll("[\\u00D3\\u00F3]", "\u043E").replaceAll("\\u00FD", "\u0443").toLowerCase();
    }

    private String resolveAmbiguity(List<Lemma> possibleLemmas) {
        String word = null;
        if (possibleLemmas.size() > 1) {
            double maxIpm = 0.0;
            for (Lemma possibleLemma : possibleLemmas) {
                String possibleWord = possibleLemma.getWord();
                FrequencyDictionaryData frequencyDictionaryData = this.frequencyDictionaryParser.getFrequencyWordData(possibleWord);
                if (frequencyDictionaryData.ipm() > maxIpm) {
                    word = possibleWord;
                    maxIpm = frequencyDictionaryData.ipm();
                }
            }
            if (maxIpm == 0) {
                word = possibleLemmas.get(0).getWord();
            }
        } else word = possibleLemmas.get(0).getWord();
        return word;
    }

    private String getPreviousWords(StringBuilder lemmatizedText, int index, int count) {
        int startIndex = lemmatizedText.lastIndexOf(" ", index);
        while (startIndex != -1 && count > 0) {
            startIndex = lemmatizedText.lastIndexOf(" ", startIndex - 1);
            count -= 1;
        }
        return startIndex != -1 ? lemmatizedText.substring(startIndex, index) : null;
    }

    private String getNextWords(StringBuilder lemmatizedText, int index, int count) {
        int endIndex = lemmatizedText.indexOf(" ", index);
        while (endIndex != -1 && count > 0) {
            endIndex = lemmatizedText.indexOf(" ", endIndex + 1);
            count -= 1;
        }
        return endIndex != -1 ? lemmatizedText.substring(index, endIndex) : null;
    }
}
