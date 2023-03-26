package nsu.belozerov;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ConsoleApp {
    private static final String CORPUS = "src/main/resources/test.xml";
    private static final int LENGTH = 3;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("input.txt"), StandardCharsets.UTF_8));
        String substring = br.readLine();
        ArticleParser parser = new ArticleParser(substring, LENGTH);
        parser.parse(CORPUS);
    }
}
