package team.nsu.cl.model;

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
    private final Model model;

    private final int SEARCH_DISTANCE = 3;

    public TextParser(FrequencyDictionaryParser frequencyDictionary, OpencorporaDictionary opencorporaDictionary, Model model) {
        this.frequencyDictionaryParser = frequencyDictionary;
        this.dictionary = opencorporaDictionary;
        this.model = model;
    }

    public List<String> parseOneText(String text) {
        return preprocess(text).stream().map(this::findOccurrences).<String>mapMulti(Iterable::forEach).toList();
    }

    private List<List<String>> preprocess(String text) {
        return split(text).stream().map(this::clear).map(this::depunctuate).toList();
    }

    private List<String> findOccurrences(List<String> sentence) {
        List<String> occurrences = new ArrayList<>();
        int length = countWords(model.word());
        for (int i = 0; i <= sentence.size() - length; i++) {
            if (lemmatize(sentence.subList(i, i + length)).equals(this.model.word())) {
                StringBuilder word = new StringBuilder(sentence.get(i));
                sentence.subList(i + 1, i + length).forEach(w -> word.append(" ").append(w));
                for (int j = 1; j <= SEARCH_DISTANCE; j++) {
                    if (i - j >= 0) {
                        if (matchesModel(sentence.get(i - j))) {
                            occurrences.add(sentence.get(i - j) + " " + word );
                        }
                    }
                    if (i + length + j - 1 < sentence.size()) {
                        if (matchesModel(sentence.get(i + length + j - 1))) {
                            occurrences.add(word + " " + sentence.get(i + length + j - 1));
                        }
                    }
                }
            }
        }
        return occurrences;
    }

    private List<String> split(String text) {
        List<String> sentences = new ArrayList<>();
        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.forLanguageTag(LANGUAGE_TAG));
        iterator.setText(text);
        int start = iterator.first();
        int end = iterator.next();
        while (end != BreakIterator.DONE) {
            sentences.add(text.substring(start, end));
            start = end;
            end = iterator.next();
        }
        return sentences;
    }

    private String clear(String text) {
        return text.replaceAll("\\u0301", "").replaceAll("[\\u00C1\\u00E1]", "\u0430").replaceAll("[\\u00C9\\u00E9]", "\u0435").replaceAll("[\\u00D3\\u00F3]", "\u043E").replaceAll("\\u00FD", "\u0443").toLowerCase();
    }

    private List<String> depunctuate(String text) {
        List<String> depunctuated = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\p{InCYRILLIC}+(-\\p{InCYRILLIC}+)?");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            depunctuated.add(text.substring(matcher.start(), matcher.end()));
        }
        return depunctuated;
    }

    private String lemmatize(List<String> words) {
        StringBuilder lemma = new StringBuilder();
        for (String word : words) {
            if (!lemma.isEmpty()) {
                lemma.append(" ");
            }
            List<Lemma> possibleLemmas = this.dictionary.getLemmas(word);
            lemma.append(Objects.nonNull(possibleLemmas) ? resolveAmbiguity(possibleLemmas) : word);
        }
        return lemma.toString();
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

    private int countWords(String word) {
        return word.split(" ").length;
    }

    private boolean matchesModel(String word) {
        List<Lemma> lemmas = dictionary.getLemmas(word);
        List<Form> forms = dictionary.getForms(word);

        Map<String, List<Grammeme>> grammemes = new HashMap<>();
        if (lemmas != null) {
            grammemes.putAll(lemmas.get(0).getGrammemes());
        }
        if (forms != null) {
            grammemes.putAll(forms.get(0).getGrammemes());
        }

        if (grammemes.size() == 0) {
            return false;
        }

        for (Map<String, String> neighbourWordGrammemes : model.surroundingWordsGrammemes()) {
            boolean everythingMatches = true;
            boolean somethingMatches = false;
            for (Map.Entry<String, String> modelGrammeme : neighbourWordGrammemes.entrySet()) {
                if (grammemes.get(modelGrammeme.getKey()) != null) {
                    String actualGrammeme = grammemes.get(modelGrammeme.getKey()).get(0).getName();
                    if (!actualGrammeme.equals(modelGrammeme.getValue())) {
                        everythingMatches = false;
                        break;
                    } else {
                        somethingMatches = true;
                    }
                }
            }
            if (!somethingMatches)
                continue;
            if (everythingMatches)
                return true;
        }

        return false;
    }
}
