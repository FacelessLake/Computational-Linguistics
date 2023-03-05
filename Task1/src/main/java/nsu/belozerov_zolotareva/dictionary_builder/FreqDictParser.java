package nsu.belozerov_zolotareva.dictionary_builder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FreqDictParser {

    private final String fileName;
    private final int lemmaIndex;
    private final int posIndex;
    private final int ipmIndex;
    private final Map<String, FreqDictData> records;

    public FreqDictParser(String fileName) {
        this.fileName = fileName;
        records = new HashMap<>();
        this.lemmaIndex = 0;
        this.posIndex = 1;
        this.ipmIndex = 2;
    }

    public void parse() {
        try (BufferedReader br =
                     new BufferedReader(new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\\s");
                String lemma = values[lemmaIndex];

                if (!records.containsKey(lemma)) {
                    records.put(lemma, new FreqDictData(values[posIndex], Double.parseDouble(values[ipmIndex])));
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FreqDictData getData(String lemma) {
        FreqDictData data = records.get(lemma);
        if (data == null) {
            return new FreqDictData("s", 0.0);
        }
        return data;
    }

}
