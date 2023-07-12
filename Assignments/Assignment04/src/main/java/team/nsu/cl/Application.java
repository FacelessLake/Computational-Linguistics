package team.nsu.cl;

import team.nsu.cl.dictionary.OpencorporaDictionary;
import team.nsu.cl.dictionary.frequency.FrequencyDictionaryParser;
import team.nsu.cl.model.Model;
import team.nsu.cl.model.ModelDeserializer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public class Application {
    private final String articlesFile;
    private String modelsFile;
    private String outputFile;
    private FileWriter outputWriter;

    private final FrequencyDictionaryParser frequencyDictionaryParser;
    private final OpencorporaDictionary dictionary;

    public Application(){
        String frequencyDictionary = "../../Resources/frequency_dict.txt";
        String opencorporaDictionary = "../../Resources/cut.opcorpora.xml";
        this.articlesFile = "../../Resources/corpus.xml";

        this.frequencyDictionaryParser = new FrequencyDictionaryParser();
        this.frequencyDictionaryParser.parse(frequencyDictionary);
        this.dictionary = OpencorporaDictionary.load(opencorporaDictionary);
    }

    public int start(String[] args) {
        if (args.length != 2) {
            System.err.println("Invalid arguments!");
            return -1;
        }
        this.modelsFile = args[0];
        this.outputFile = args[1];
        process();
        return 0;
    }

    void process() {
        ModelDeserializer deserializer = new ModelDeserializer();
        List<Model> modelList;
        try {
            modelList = deserializer.deserialize(modelsFile);
            outputWriter = new FileWriter(outputFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Model model : modelList) {
            ArticleParser articleParser = new ArticleParser(frequencyDictionaryParser, dictionary, model);
            List<String> allFragmentsList = articleParser.parse(articlesFile);

            List<String> outputList = allFragmentsList.stream().distinct().sorted().toList();
            int counter = allFragmentsList.size();
            StringBuilder answer = new StringBuilder("<" + model.id() + ", " + counter + ", ");
            for (String fragment : outputList) {
                answer.append(fragment).append(",\n");
            }
            answer.append(">\n");

            try {
                outputWriter.write(answer.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            outputWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
