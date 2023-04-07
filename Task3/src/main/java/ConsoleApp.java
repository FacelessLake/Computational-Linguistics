import NGramDictionary.Dictionary;
import ngram.NGram;
import ngram.NGramPrinter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ConsoleApp {
    private static final String CORPUS = "src/main/resources/test.xml";

    public static void main(String[] args) throws IOException {
        String frequencyDictionary = "src/main/resources/frequency_dict.txt";
        String opencorporaDictionary = "src/main/resources/dict.opcorpora.xml";
        ArticleParser parser = new ArticleParser(frequencyDictionary, opencorporaDictionary,0.5);
        List<NGram> nGrams = parser.parse(CORPUS);
        NGramPrinter printer = new NGramPrinter(nGrams);
        printer.printNGrams();

        Dictionary dictionary = new Dictionary("src/main/resources/output.txt");
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new FileInputStream("src/main/resources/input.txt"), StandardCharsets.UTF_8));
        String line = bufferedReader.readLine();
        dictionary.findNGramsByWord(line);
//        Application application = new Application();
//        System.exit(application.start(args));
    }
}
