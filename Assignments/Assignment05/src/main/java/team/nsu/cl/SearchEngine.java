package team.nsu.cl;

import team.nsu.cl.dictionary.Lemma;
import team.nsu.cl.dictionary.OpencorporaDictionary;
import team.nsu.cl.index.Corpus;
import team.nsu.cl.index.Document;
import team.nsu.cl.index.Occurrence;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SearchEngine {

    private record DocumentRelevance(int documentNumber, int score) {}

    static class DocumentRelevanceComparator implements Comparator<DocumentRelevance>{
        public int compare(DocumentRelevance d1, DocumentRelevance d2){
            if (d1.score == d2.score) return 0;
            return d1.score > d2.score ? -1 : 1;
        }
    }

    private final HashMap<String, List<String>> thesaurus;
    private final Corpus corpus;
    private final OpencorporaDictionary dictionary;

    public SearchEngine(HashMap<String, List<String>> thesaurus, Corpus corpus, OpencorporaDictionary dictionary) {
        this.thesaurus = thesaurus;
        this.corpus = corpus;
        this.dictionary = dictionary;
    }


    private int calculateWSingle(List<String> terms, int textNumber) {
        for (String term : terms) {
            Set<Occurrence> occurrences = corpus.findOccurrences(term);
            if (occurrences != null) {
                if (occurrences.stream().anyMatch(e -> e.document() == textNumber)) {
                    return 1;
                }
            }
        }
        return 0;
    }


    private int calculateWAllWords(List<String> terms, int textNumber) {
        for (String term : terms) {
            Set<Occurrence> occurrences = corpus.findOccurrences(term);
            if (occurrences == null) {
                return 0;
            }
            if (occurrences.stream().noneMatch(e -> e.document() == textNumber)) {
                return 0;
            }
        }
        return 1;
    }


    private int calculateWPhase(List<String> terms, int textNumber) {
        List<Set<Integer>> sentenceNumbers = new ArrayList<>();
        for (String term : terms) {
            Set<Occurrence> occurrences = corpus.findOccurrences(term);
            Set<Integer> set;
            if (occurrences != null) {
                set = occurrences.stream()
                        .filter(e -> e.document() == textNumber)
                        .map(Occurrence::sentence)
                        .collect(Collectors.toSet());
            } else {
                set = new HashSet<>();
            }
            sentenceNumbers.add(set);
        }
        Set<Integer> set0 = sentenceNumbers.get(0);
        for (Set<Integer> setN : sentenceNumbers) {
            set0.retainAll(setN);
        }
        return set0.isEmpty() ? 0 : 1;
    }


    public int calculateWHalfPhase(List<String> terms, int textNumber) {
        List<Integer> sentenceNumbers = new ArrayList<>();
        for (String term : terms) {
            Set<Occurrence> occurrences = corpus.findOccurrences(term);
            Set<Integer> set;
            if (occurrences != null) {
                set = occurrences.stream()
                        .filter(e -> e.document() == textNumber)
                        .map(Occurrence::sentence)
                        .collect(Collectors.toSet());
            } else {
                set = new HashSet<>();
            }
            sentenceNumbers.addAll(set);
        }
        Optional<Map.Entry<Integer, Long>> max = sentenceNumbers.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue());
        return max.filter(integerLongEntry -> integerLongEntry.getValue() > (long) terms.size() / 2).map(integerLongEntry -> 1).orElse(0);
    }



    public void search(String query) {
        String[] split = query.split(" ");
        List<String> queryTerms = new ArrayList<>();
        List<String> queryTermsExtended = new ArrayList<>();
        for (String word : split) {
            List<Lemma> lemmas = dictionary.getLemmas(word);
            if (lemmas == null || lemmas.isEmpty()) {
                continue;
            }
            Lemma lemma = lemmas.get(0);
            String pos = lemma.getGrammemes().get(0).getName();
            if (pos.equals("PREP") || pos.equals("CONJ") ||
                    pos.equals("PRCL") || pos.equals("INTJ") ||
                    pos.equals("NPRO")) {
                continue;
            }
            queryTerms.add(lemma.getWord());
            queryTermsExtended.add(lemma.getWord());
            if (thesaurus.get(lemma.getWord()) != null) {
                queryTermsExtended.addAll(thesaurus.get(lemma.getWord()));
            }
        }

        if (queryTerms.isEmpty()) {
            System.out.println("No relevant words in query!");
            return;
        }

        List<DocumentRelevance> relevance = new ArrayList<>();
        for (int i = 1; i <= corpus.getDocumentsNumber(); i++) {
            int score = calculateWSingle(queryTermsExtended, i) * 2 +
                    calculateWAllWords(queryTermsExtended, i) * 7 +
                    calculateWPhase(queryTerms, i) * 10 +
                    calculateWHalfPhase(queryTerms, i) * 6;
            if (score == 0) continue;
            relevance.add(new DocumentRelevance(i, score));
        }

        relevance.sort(new DocumentRelevanceComparator());

        for (int i = 0; i < 5; i++) {
            if (relevance.size() <= i) break;

            System.out.println("\n\n=========================================================");
            int documentNumber = relevance.get(i).documentNumber;
            Document document = corpus.getDocument(documentNumber);
            System.out.println("Title: \"" + document.getTitle() + "\"");
            System.out.println("Number: \"" + documentNumber + "\"");
            System.out.println("Relevance: " + relevance.get(i).score);

            Set<Integer> sentenceNumbers = new HashSet<>();
            for (String term : queryTerms) {
                //System.out.println("For term: " + term);
                Set<Occurrence> occurrences = corpus.findOccurrences(term);
                //System.out.println(occurrences);
                if (occurrences != null) {
                    Set<Integer> set = new HashSet<>();
                    List<Occurrence> occurrencesInDoc = occurrences.stream().filter(e -> e.document() == documentNumber).toList();
                    for (Occurrence occurrence : occurrencesInDoc) {
                        set.add(occurrence.sentence());
                    }
                    sentenceNumbers.addAll(set);
                }
            }

            for (Integer sentenceNumber : sentenceNumbers) {
                if (sentenceNumber > 1) {
                    System.out.println(document.getSentence(sentenceNumber - 1));
                }
                System.out.println(document.getSentence(sentenceNumber));
                if (sentenceNumber < document.getSentencesAmount() - 1) {
                    System.out.println(document.getSentence(sentenceNumber + 1));
                }
                System.out.println("<" + documentNumber + ", " + sentenceNumber + ", " + relevance.get(i).score + ">\n");
            }
            System.out.println("=========================================================");
        }

    }
}
