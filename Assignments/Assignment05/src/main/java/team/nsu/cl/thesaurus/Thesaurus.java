package team.nsu.cl.thesaurus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Thesaurus {
    String keyword;
    String[] synonyms;
    String[] partial;
    String[] association;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String[] getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(String[] synonyms) {
        this.synonyms = synonyms;
    }

    public String[] getPartial() {
        return partial;
    }

    public void setPartial(String[] partial) {
        this.partial = partial;
    }

    public String[] getAssociation() {
        return association;
    }

    public void setAssociation(String[] association) {
        this.association = association;
    }

    public List<String> getAllConnectedWords() {
        List<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(synonyms));
        list.addAll(Arrays.asList(partial));
        list.addAll(Arrays.asList(association));
        return list;
    }
}
