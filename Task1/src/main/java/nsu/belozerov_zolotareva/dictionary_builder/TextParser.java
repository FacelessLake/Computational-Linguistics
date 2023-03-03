package nsu.belozerov_zolotareva.dictionary_builder;

import nsu.belozerov_zolotareva.lematization.Lemma;
import nsu.belozerov_zolotareva.lematization.MorphologyDict;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextParser {

    private final Writer debugWriter;
    private final Writer outputWriter;
    private final FreqDictParser freqDictParser;
    private final Map<WordData, Double> lemmasFrequency;
    private final MorphologyDict ocd;

    public TextParser() {
        try {
            debugWriter = new OutputStreamWriter(new FileOutputStream("debug.txt"), StandardCharsets.UTF_8);
            outputWriter = new OutputStreamWriter(new FileOutputStream("res.txt"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        freqDictParser = new FreqDictParser("src/main/resources/frequency_dict.txt");
        freqDictParser.parse();
        lemmasFrequency = new HashMap<>();
        ocd = MorphologyDict.load("src/main/resources/dict.opcorpora.xml");
    }

    public List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        String clearText = text.replaceAll("\\u0301", "").toLowerCase();
        Pattern pattern = Pattern.compile("\\p{InCYRILLIC}+(-\\p{InCYRILLIC}+)?");
        Matcher matcher = pattern.matcher(clearText);
        while (matcher.find()) {
            tokens.add(clearText.substring(matcher.start(), matcher.end()));
        }
        return tokens;
    }

    public String renamerToCorpora(String label) {
        return switch (label) {
            case "s" -> "NOUN";
            case "a" -> "ADJF";
            case "v" -> "VERB";
            case "anum", "num" -> "NUMR";
            case "adv", "advpro" -> "ADVB";
            case "apro", "spro" -> "NPRO";
            case "pr" -> "PREP";
            case "conj" -> "CONJ";
            case "part" -> "PRCL";
            case "intj" -> "INTJ";
            default -> label;
        };
    }

    public WordData resolveAmbiguity(ArrayList<Lemma> possibleLemmas) {
        String word = null;
        String wordPos = null;
        WordData wordData;

        if (possibleLemmas.size() == 1) {
            Lemma lemma = possibleLemmas.get(0);
            word = lemma.getWord();
            /*
            if (lemma.getGrammemes().get(0) == null) {
                try {
                    debugWriter.write(word + "\n");
                    debugWriter.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                continue;
            }
             */
            wordPos = lemma.getGrammemes().get(0).getName();

        } else {
            Double maxIpm = 0.0;

            for (Lemma possibleLemma : possibleLemmas) {
                String possibleWord = possibleLemma.getWord();
                FrequencyWordData possibleData = freqDictParser.getData(possibleWord);

                if (possibleData.ipm() > maxIpm) {
                    word = possibleWord;
                    wordPos = renamerToCorpora(possibleData.pos());
                    maxIpm = possibleData.ipm();
                }
            }

            if (maxIpm == 0) {
                word = possibleLemmas.get(0).getWord();
                /*
                if (possibleLemmas.get(0).getGrammemes().get(0) == null) {
                    try {
                        debugWriter.write(word + "\n");
                        debugWriter.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    continue;
                }
                 */
                wordPos = possibleLemmas.get(0).getGrammemes().get(0).getName();
            }

        }

        wordData = new WordData(word, wordPos);
        return wordData;
    }


    public void parseOneText(String text) {
        List<String> tokens = this.tokenize(text);

        for (String token : tokens) {
            List<Lemma> lemmaList = ocd.getLemmas(token);
            if (lemmaList == null) {
                try {
                    debugWriter.write("Not in dictionary: " + token + "\n");
                    debugWriter.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                continue;
            }

            ArrayList<Lemma> possibleLemmas = new ArrayList<>(lemmaList);
            WordData wordData = this.resolveAmbiguity(possibleLemmas);

            Double lemmaIpm = lemmasFrequency.get(wordData);
            if (lemmaIpm != null) {
                lemmaIpm++;
            } else {
                lemmaIpm = 1.0;
            }
            lemmasFrequency.put(wordData, lemmaIpm);
        }
    }

    public static HashMap<WordData, Double> sortByValue(Map<WordData, Double> hm) {

        List<Map.Entry<WordData, Double>> list = new LinkedList<>(hm.entrySet());

        list.sort((o1, o2) -> -(o1.getValue()).compareTo(o2.getValue()));


        HashMap<WordData, Double> temp = new LinkedHashMap<>();
        for (Map.Entry<WordData, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public void printAll() throws IOException {
        System.out.println(lemmasFrequency.size());
        Map<WordData, Double> result = sortByValue(lemmasFrequency);
        for (Map.Entry<WordData, Double> res : result.entrySet()) {
            StringBuilder str = new StringBuilder();
            str.append("<");
            str.append(res.getKey().lemma());
            str.append(", ");
            str.append(res.getKey().pos());
            str.append(", ");
            str.append(res.getValue());
            str.append(">\n");
            outputWriter.write(str.toString());
            outputWriter.flush();
        }
    }

}