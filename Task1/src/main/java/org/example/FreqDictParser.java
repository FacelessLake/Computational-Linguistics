package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class FreqDictParser {

    private final String fileName;
    private final int lemmaIndex;
    private final int posIndex;
    private final int ipmIndex;
    private final Map<String, FrequencyWordData> records;

    public FreqDictParser(String fileName) {
        this.fileName = fileName;
        records = new HashMap<>();
        this.lemmaIndex = 0;
        this.posIndex = 1;
        this.ipmIndex = 2;
    }

    public void parse() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\\s");
                String lemma = values[lemmaIndex];

                if (!records.containsKey(lemma)) {
                    records.put(lemma, new FrequencyWordData(values[posIndex], Double.parseDouble(values[ipmIndex])));
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FrequencyWordData getData(String lemma) {
        FrequencyWordData data = records.get(lemma);
        if (data == null){
            return new FrequencyWordData("s",0.0);
        }
        return data;
    }

}
