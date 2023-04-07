package NGramDictionary;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DictionaryLoader {

    private final List<DictionaryNGram> dictionaryNGrams;
    private final BufferedReader dictionaryReader;

    public DictionaryLoader(String dictionaryFileName) throws FileNotFoundException {
        dictionaryNGrams = new ArrayList<>();
        dictionaryReader = new BufferedReader(new InputStreamReader(new FileInputStream(dictionaryFileName), StandardCharsets.UTF_8));
    }

    public List<DictionaryNGram> load() {
        try {
            String line;
            while ((line = dictionaryReader.readLine()) != null) {
                String[] values = line.split("[<>,]");
                dictionaryNGrams.add(new DictionaryNGram(values[1],
                        Integer.parseInt(values[2].trim()),
                        Double.parseDouble(values[3].trim()),
                        Double.parseDouble(values[4].trim())));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return dictionaryNGrams;
    }
}
