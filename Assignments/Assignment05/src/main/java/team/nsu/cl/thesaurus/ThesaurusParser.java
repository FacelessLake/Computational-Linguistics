package team.nsu.cl.thesaurus;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ThesaurusParser {

    Gson gson = new Gson();
    Type thesaurusListType = new TypeToken<ArrayList<Thesaurus>>() {}.getType();

    public HashMap<String,List<String>> parseThesaurus(String filePath) {
        List<Thesaurus> additionalWords;
        try {
            additionalWords = gson.fromJson(Files.newBufferedReader(Path.of(filePath), StandardCharsets.UTF_8), thesaurusListType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HashMap<String, List<String>> additionalWordsHashMap = new HashMap<>();
        for (Thesaurus thesaurus : additionalWords){
            ArrayList<String> additionalWordsList = new ArrayList<>();
            additionalWordsList.addAll(Arrays.asList(thesaurus.getSynonyms()));
            additionalWordsList.addAll(Arrays.asList(thesaurus.getPartial()));
            additionalWordsList.addAll(Arrays.asList(thesaurus.getAssociation()));
            additionalWordsHashMap.put(thesaurus.getKeyword(),additionalWordsList);
        }
        return additionalWordsHashMap;
    }


}
