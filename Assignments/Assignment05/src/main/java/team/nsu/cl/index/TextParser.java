package team.nsu.cl.index;

import java.text.BreakIterator;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import team.nsu.cl.dictionary.*;
import team.nsu.cl.dictionary.frequency.FrequencyDictionaryData;
import team.nsu.cl.dictionary.frequency.FrequencyDictionaryParser;

@SuppressWarnings("SpellCheckingInspection")
public class TextParser {
    private static final String LANGUAGE_TAG = "ru-RU";
    private final FrequencyDictionaryParser frequencyDictionaryParser;
    private final OpencorporaDictionary dictionary;
    private final Corpus corpus;
    private Document document;
    private int docCount;
    private int sentenceCount;

    public TextParser(FrequencyDictionaryParser frequencyDictionaryParser, OpencorporaDictionary dictionary) {
        this.frequencyDictionaryParser = frequencyDictionaryParser;
        this.dictionary = dictionary;
        this.corpus = new Corpus();
        this.docCount = 0;
        this.sentenceCount = 0;
    }

    public void increaseDocCount(String title) {
        this.document = new Document(title);
        this.corpus.addDocument(this.document);
        this.docCount += 1;
        this.sentenceCount = 0;
    }

    public void parseOneText(String text) {
        String clearedText = clear(text);
        for (String sentence : split(clearedText)) {
            this.sentenceCount += 1;
            this.document.addSentence(sentence);
            updateIndices(sentence);
        }
    }

    public Corpus buildIndices() {
        return this.corpus;
    }

    @SuppressWarnings("UnnecessaryUnicodeEscape")
    private String clear(String text) {
        return text.replaceAll("\\u0301", "").replaceAll("[\\u00C1\\u00E1]", "\u0430").replaceAll("[\\u00C9\\u00E9]", "\u0435").replaceAll("[\\u00D3\\u00F3]", "\u043E").replaceAll("\\u00FD", "\u0443");//.toLowerCase();
    }

    private List<String> split(String text) {
        List<String> sentences = new ArrayList<>();
        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.forLanguageTag(LANGUAGE_TAG));
        iterator.setText(text);
        int start = iterator.first();
        int end = iterator.next();
        while (end != BreakIterator.DONE) {
            sentences.add(text.substring(start, end).toLowerCase());
            start = end;
            end = iterator.next();
        }
        return sentences;
    }

    private void updateIndices(String sentence) {
        Pattern pattern = Pattern.compile("\\p{InCYRILLIC}+(-\\p{InCYRILLIC}+)?");
        Matcher matcher = pattern.matcher(sentence);
        while (matcher.find()) {
            String lemma = lemmatize(sentence.substring(matcher.start(), matcher.end()));
            this.corpus.addOccurrence(lemma, new Occurrence(this.docCount, this.sentenceCount));
        }
    }

    private String lemmatize(String word) {
        List<Lemma> possibleLemmas = this.dictionary.getLemmas(word);
        return Objects.nonNull(possibleLemmas) ? resolveAmbiguity(possibleLemmas) : word;
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
}