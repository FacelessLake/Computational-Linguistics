package NGramDictionary;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Dictionary {
    private final List<DictionaryNGram> nGrams;
    private final Map<String, DictionaryNGram> phraseToNGram; // Временная мапа для построения дерева
    private final Map<String, List<DictionaryNGram>> wordsToNGrams;

    public Dictionary(String dictionaryFileName) throws FileNotFoundException {
        DictionaryLoader loader = new DictionaryLoader(dictionaryFileName);
        nGrams = loader.load();
        phraseToNGram = new HashMap<>();
        wordsToNGrams = new HashMap<>();

        buildTree();
    }

    private void putOneWordInWordsToNGrams(String key, DictionaryNGram value) {
        if (!wordsToNGrams.containsKey(key)) {
            wordsToNGrams.put(key, new ArrayList<>());
        }
        if (!wordsToNGrams.get(key).contains(value)) {
            wordsToNGrams.get(key).add(value);
        }
    }

    private void putInWordsToNGrams(DictionaryNGram nGram) {
        for (String word : nGram.getContentSplit()) {
            putOneWordInWordsToNGrams(word, nGram);
        }
    }

    // ищет все N-грамы длины 2, запускает поиск всех более длинных N-грам
    private void buildTree() {
        for (DictionaryNGram nGram : nGrams) {
            if (nGram.getContentLength() == 2) {
                putInWordsToNGrams(nGram);
                phraseToNGram.put(nGram.getContent(), nGram);
            }
        }

        int i = 3;
        while (buildNextLevel(i)) {
            i++;
        }
    }

    // ищет все N-грамы длины k
    private boolean buildNextLevel(int k) {
        boolean hasKGrams = false;
        for (DictionaryNGram nGram : nGrams) {
            if (nGram.getContentLength() == k) {
                DictionaryNGram parent1 = phraseToNGram.get(nGram.getParent1Content());
                DictionaryNGram parent2 = phraseToNGram.get(nGram.getParent2Content());
                if (parent1 != null) {
                    parent1.addChild(nGram);
                }
                if (parent2 != null) {
                    parent2.addChild(nGram);
                }
                if (parent1 == null && parent2 == null) {
                    putInWordsToNGrams(nGram);
                }
                phraseToNGram.put(nGram.getContent(), nGram);
            }
        }
        return hasKGrams;
    }

    // возвращает список всех расширений N-граммы из дерева (+ исходную N-грамму)
    private List<DictionaryNGram> getSubtree(DictionaryNGram nGram) {
        List<DictionaryNGram> res = new ArrayList<>();
        res.add(nGram);
        for (DictionaryNGram child : nGram.getChildren()) {
            res.addAll(getSubtree(child));
        }
        return res;
    }

    // получает объединение всех списков (N-грамы в результате не повторяются)
    private List<DictionaryNGram> getUnion(List<List<DictionaryNGram>> lists) {
        List<DictionaryNGram> res = new ArrayList<>();
        for (List<DictionaryNGram> list : lists) {
            for (DictionaryNGram nGram : list) {
                if (!res.contains(nGram)) {
                    res.add(nGram);
                }
            }
        }
        return res;
    }

    // печатает все в списке в файл
    private void printAllNGrams(List<DictionaryNGram> list) throws IOException {
        Writer outputWriter = new OutputStreamWriter(new FileOutputStream("result.txt"), StandardCharsets.UTF_8);
        if (list == null) return;
        for (DictionaryNGram nGram : list) {
            outputWriter.write(nGram.toString() + "\n");
        }
        outputWriter.flush();
    }


    // находит все N-граммы, включающие заданное слово
    public List<DictionaryNGram> findNGramsByWord(String word) {
        List<List<DictionaryNGram>> allWordNGrams = new ArrayList<>();
        List<DictionaryNGram> shortNGrams = wordsToNGrams.get(word);
        if (shortNGrams == null) {
            return new ArrayList<>();
        }
        for (DictionaryNGram nGram : shortNGrams) {
            allWordNGrams.add(getSubtree(nGram));
        }
        //printAllNGrams(getUnion(allWordNGrams));
        return getUnion(allWordNGrams);
    }


    // находит все вложенные N-граммы
    public List<DictionaryNGram> findNestedNGrams(String string) {
        List<DictionaryNGram> allStringNGrams = new ArrayList<>();
        String[] split = string.split("\\s");
        for (int i = 2; i <= split.length; i++) {
            for (int j = 0; j < split.length - i + 1; j++) {
                String window = "";
                for (int k = 0; k < i; k++) {
                    window = window + " " + split[j + k];
                }
                DictionaryNGram nGram = phraseToNGram.get(window.trim());
                if (nGram != null && !allStringNGrams.contains(nGram)) allStringNGrams.add(nGram);
            }
        }
        //printAllNGrams(allStringNGrams);
        return allStringNGrams;
    }
}