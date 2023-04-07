package ngram;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class NGramPrinter {
    private final Writer outputWriter;
    private final List<NGram> nGramList;

    public NGramPrinter(List<NGram> nGramList) throws FileNotFoundException {
        this.nGramList = nGramList;
        File targetFile = new File("src/main/resources", "output.txt");
        outputWriter = new OutputStreamWriter(new FileOutputStream(targetFile), StandardCharsets.UTF_8);
    }

    public void printNGrams() throws IOException {
        ArrayList<NGram> result = new ArrayList<>(nGramList);
        result.sort((o1, o2) -> -o1.compareTo(o2));

        for (NGram res : result) {
            StringBuilder str = new StringBuilder();
            str.append("<");
            str.append(res.toString().trim());
            str.append(", ");
            str.append(res.getAbsoluteFrequency());
            str.append(", ");
            str.append((int)res.getTextFrequency());
            str.append(", ");
            str.append(res.getIdf());
            str.append(">\n");
            outputWriter.write(str.toString());
        }
        outputWriter.flush();
    }
}
