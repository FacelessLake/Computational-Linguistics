package org.example;

import org.example.lematization.Lemma;
import org.example.lematization.OpencorporaDictionary;

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
    private final OpencorporaDictionary ocd;

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
        ocd = OpencorporaDictionary.load("src/main/resources/dict.opcorpora.xml");
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
        String newLabel = label;
        switch (label) {
            case "s":
                newLabel = "NOUN";
            case "a":
                newLabel = "ADJF";
            case "v":
                newLabel = "VERB";
            case "anum":
            case "num":
                newLabel = "NUMR";
            case "adv":
            case "advpro":
                newLabel = "ADVB";
            case "apro":
            case "spro":
                newLabel = "NPRO";
            case "pr":
                newLabel = "PREP";
            case "conj":
                newLabel = "CONJ";
            case "part":
                newLabel = "PRCL";
            case "intj":
                newLabel = "INTJ";
        }
        return newLabel;
    }

    public void parseOneText(String text) {
        List<String> tokens = this.tokenize(text);

        for (String token : tokens) {

            Set<Lemma> lemmaSet = ocd.getLemmas(token);
            if (lemmaSet == null){
                try {
                    debugWriter.write("Not in dictionary: "+token+"\n");
                    debugWriter.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                continue;
            }
            ArrayList<Lemma> possibleLemmas = new ArrayList<>(lemmaSet);
            String word = null;
            String wordPos = null;
            WordData wordData;

            if (possibleLemmas.size() == 1) {
                Lemma lemma = possibleLemmas.get(0);
                word = lemma.getWord();
                if (lemma.getProperties().getGrammemes().get(0) == null){
                    try {
                        debugWriter.write(word+"\n");
                        debugWriter.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    continue;
                }
                wordPos = lemma.getProperties().getGrammemes().get(0).getName();

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
                    if (possibleLemmas.get(0).getProperties().getGrammemes().get(0) == null){
                        try {
                            debugWriter.write(word+"\n");
                            debugWriter.flush();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        continue;
                    }
                    wordPos = possibleLemmas.get(0).getProperties().getGrammemes().get(0).getName();
                }

            }

            wordData = new WordData(word, wordPos);
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

        List<Map.Entry<WordData, Double>> list = new LinkedList<Map.Entry<WordData, Double>>(hm.entrySet());

        list.sort(new Comparator<Map.Entry<WordData, Double>>() {
            public int compare(Map.Entry<WordData, Double> o1,
                               Map.Entry<WordData, Double> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });


        HashMap<WordData, Double> temp = new LinkedHashMap<WordData, Double>();
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
