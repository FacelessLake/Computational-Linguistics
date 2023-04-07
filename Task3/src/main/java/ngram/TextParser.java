package ngram;

import dictionary.Dictionary;
import dictionary.Lemma;
import dictionary.OpencorporaDictionary;
import dictionary.frequency.FrequencyDictionaryData;
import dictionary.frequency.FrequencyDictionaryParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("SpellCheckingInspection")
public class TextParser {
    private final FrequencyDictionaryParser frequencyDictionaryParser;
    private final Dictionary dictionary;
    private final Map<NGramHolder, NGramDescriptor> rootNGrams;
    private final double threshold;
    private final StringBuilder lemmatizedText;
    private final NavigableMap<Integer, Integer> textIndices;

    public TextParser(String frequencyDictionary, String opencorporaDictionary, double threshold) {
        this.frequencyDictionaryParser = new FrequencyDictionaryParser();
        this.frequencyDictionaryParser.parse(frequencyDictionary);
        this.dictionary = OpencorporaDictionary.load(opencorporaDictionary);
        this.threshold = threshold;
        this.rootNGrams = new HashMap<>();
        this.lemmatizedText = new StringBuilder();
        this.textIndices = new TreeMap<>();
        this.textIndices.put(0, 0);
    }

    public void increaseDocCount() {
        this.textIndices.put(this.lemmatizedText.length(), this.textIndices.get(this.textIndices.lastKey()) + 1);
    }

    public void parseOneText(String text) {
        int startIndex = this.lemmatizedText.length();
        lemmatize(text);
        while (startIndex != -1) {
            int endIndex = getNextWords(startIndex, 2);
            if (endIndex != -1) {
                NGramHolder nGramHolder = new NGramHolder(this.lemmatizedText, startIndex, endIndex);
                this.rootNGrams.putIfAbsent(nGramHolder, new NGramDescriptor(endIndex - startIndex));
                NGramDescriptor nGramDescriptor = this.rootNGrams.get(nGramHolder);
                updateNGram(nGramDescriptor, startIndex);
            }
            startIndex = getNextWords(startIndex, 1);
            if (startIndex != -1) {
                startIndex += 1;
            }
        }
    }

    public List<NGram> extract() {
        Map<NGramDescriptor, NGram> stableNGrams = new HashMap<>();
        extractNGrams(NGramType.ROOT_NGRAM, this.rootNGrams, stableNGrams);
        return List.copyOf(stableNGrams.values());
    }

    private void lemmatize(String text) {
        String clearText = preprocess(text);
        Pattern pattern = Pattern.compile("\\p{InCYRILLIC}+(-\\p{InCYRILLIC}+)?");
        Matcher matcher = pattern.matcher(clearText);
        while (matcher.find()) {
            if (!this.lemmatizedText.isEmpty()) {
                this.lemmatizedText.append(" ");
            }
            String word = clearText.substring(matcher.start(), matcher.end());
            List<Lemma> possibleLemmas = this.dictionary.getLemmas(word);
            this.lemmatizedText.append(Objects.nonNull(possibleLemmas) ? resolveAmbiguity(possibleLemmas) : word);
        }
    }

    private void extractNGrams(NGramType nGramType, Map<NGramHolder, NGramDescriptor> rootNGrams, Map<NGramDescriptor, NGram> stableNGrams) {
        rootNGrams.values().removeIf(nGramDescriptor -> nGramDescriptor.getOccurrencesCount() < 10);
        Map<NGramHolder, NGramDescriptor> leftHandExpansions = new HashMap<>();
        Map<NGramHolder, NGramDescriptor> rightHandExpansions = new HashMap<>();
        rootNGrams.values().forEach(nGramDiscriptor -> processNGram(nGramType, nGramDiscriptor, stableNGrams, leftHandExpansions, rightHandExpansions));
        rightHandExpansions.entrySet().stream().filter(nGramEntry -> leftHandExpansions.containsKey(nGramEntry.getKey())).forEach(nGramEntry -> leftHandExpansions.get(nGramEntry.getKey()).addParent(nGramEntry.getValue().getParents().get(0)));
        rightHandExpansions.keySet().removeIf(leftHandExpansions::containsKey);
        rootNGrams.clear();
        if (!leftHandExpansions.isEmpty()) {
            extractNGrams(NGramType.LEFT_HAND_EXPANSION, leftHandExpansions, stableNGrams);
        }
        if (!rightHandExpansions.isEmpty()) {
            extractNGrams(NGramType.RIGHT_HAND_EXPANSION, rightHandExpansions, stableNGrams);
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

    private void updateNGram(NGramDescriptor nGramDescriptor, int startIndex) {
        nGramDescriptor.addStartIndex(startIndex);
        nGramDescriptor.increaseOccurrencesCount();
        List<Integer> startIndices = nGramDescriptor.getStartIndices();
        if (startIndices.size() < 2 || this.textIndices.get(this.textIndices.floorKey(startIndices.get(startIndices.size() - 2))) < (this.textIndices.get(this.textIndices.floorKey(startIndices.get(startIndices.size() - 1))))) {
            nGramDescriptor.increaseTextCount();
        }
    }

    private void processNGram(NGramType nGramType, NGramDescriptor nGramDescriptor, Map<NGramDescriptor, NGram> stableNGrams, Map<NGramHolder, NGramDescriptor> leftHandExpansions, Map<NGramHolder, NGramDescriptor> rightHandExpansions) {
        NGramDescriptor leftNGramDescriptor = null;
        NGramDescriptor rightNGramDescriptor = null;
        for (int startIndex : nGramDescriptor.getStartIndices()) {
            int leftStartIndex = getPreviousWords(startIndex, 1);
            if (leftStartIndex != -1) {
                NGramHolder leftNGramHolder = new NGramHolder(this.lemmatizedText, leftStartIndex, startIndex + nGramDescriptor.getLength());
                leftHandExpansions.putIfAbsent(leftNGramHolder, new NGramDescriptor(startIndex + nGramDescriptor.getLength() - leftStartIndex, nGramDescriptor));
                NGramDescriptor leftNGramDescriptorCandidate = leftHandExpansions.get(leftNGramHolder);
                updateNGram(leftNGramDescriptorCandidate, leftStartIndex);
                if (Objects.isNull(leftNGramDescriptor) || leftNGramDescriptorCandidate.getOccurrencesCount() > leftNGramDescriptor.getOccurrencesCount()) {
                    leftNGramDescriptor = leftNGramDescriptorCandidate;
                }
            }
            int rightEndIndex = getNextWords(startIndex + nGramDescriptor.getLength(), 1);
            if (rightEndIndex != -1) {
                NGramHolder rightNGramHolder = new NGramHolder(this.lemmatizedText, startIndex, rightEndIndex);
                rightHandExpansions.putIfAbsent(rightNGramHolder, new NGramDescriptor(rightEndIndex - startIndex, nGramDescriptor));
                NGramDescriptor rightNGramDescriptorCandidate = rightHandExpansions.get(rightNGramHolder);
                updateNGram(rightNGramDescriptorCandidate, startIndex);
                if (Objects.isNull(rightNGramDescriptor) || rightNGramDescriptorCandidate.getOccurrencesCount() > rightNGramDescriptor.getOccurrencesCount()) {
                    rightNGramDescriptor = rightNGramDescriptorCandidate;
                }
            }
        }
        if (Objects.nonNull(rightNGramDescriptor) && Objects.nonNull(leftNGramDescriptor) && isNGramStable(nGramDescriptor, leftNGramDescriptor, rightNGramDescriptor)) {
            NGram[] parents = nGramDescriptor.getParents().stream().map(stableNGrams::get).filter(Objects::nonNull).toArray(NGram[]::new);
            NGramType type;
            if (parents.length > 1) {
                type = NGramType.MULTI_EXPANSION;
            } else if (parents.length != 0) {
                type = nGramType;
            } else type = NGramType.ROOT_NGRAM;
            String content = getNGramContent(type, nGramDescriptor);
            int absoluteFrequency = nGramDescriptor.getOccurrencesCount();
            double textFrequency = ((double) nGramDescriptor.getTextCount()) / ((double) this.textIndices.get(this.textIndices.lastKey()) + 1);

            double idf = Math.log10(((double) this.textIndices.get(this.textIndices.lastKey())) / ((double) nGramDescriptor.getTextCount()));
            stableNGrams.put(nGramDescriptor, new NGram(content, absoluteFrequency, textFrequency, idf, type, parents));
        }
    }

    private boolean isNGramStable(NGramDescriptor nGramDescriptor, NGramDescriptor rightNGramDescriptor, NGramDescriptor leftNGramDescriptor) {
        return ((double) leftNGramDescriptor.getOccurrencesCount()) / ((double) nGramDescriptor.getOccurrencesCount()) <= this.threshold && ((double) rightNGramDescriptor.getOccurrencesCount()) / ((double) nGramDescriptor.getOccurrencesCount()) <= this.threshold;
    }

    private String getNGramContent(NGramType nGramType, NGramDescriptor nGramDescriptor) {
        return switch (nGramType) {
            case ROOT_NGRAM ->
                    this.lemmatizedText.substring(nGramDescriptor.getStartIndices().get(0), nGramDescriptor.getStartIndices().get(0) + nGramDescriptor.getLength());
            case LEFT_HAND_EXPANSION, MULTI_EXPANSION ->
                    this.lemmatizedText.substring(nGramDescriptor.getStartIndices().get(0), getNextWords(nGramDescriptor.getStartIndices().get(0), 1));
            case RIGHT_HAND_EXPANSION ->
                    this.lemmatizedText.substring(getPreviousWords(nGramDescriptor.getStartIndices().get(0) + nGramDescriptor.getLength() - 1, 1), nGramDescriptor.getStartIndices().get(0) + nGramDescriptor.getLength());
        };
    }

    @SuppressWarnings("SameParameterValue")
    private int getPreviousWords(int index, int count) {
        int startIndex = this.lemmatizedText.lastIndexOf(" ", index);
        while (startIndex != -1 && count > 0) {
            startIndex = this.lemmatizedText.lastIndexOf(" ", startIndex - 1);
            count -= 1;
        }
        return startIndex != -1 ? startIndex + 1 : startIndex;
    }

    private int getNextWords(int index, int count) {
        int endIndex = index < this.lemmatizedText.length() ? index : -1;
        while (endIndex != -1 && count > 0) {
            endIndex = this.lemmatizedText.indexOf(" ", endIndex + 1);
            count -= 1;
        }
        return endIndex;
    }
}
