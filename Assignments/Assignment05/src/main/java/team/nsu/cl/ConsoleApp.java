package team.nsu.cl;

import team.nsu.cl.dictionary.OpencorporaDictionary;

public class ConsoleApp {
    public static void main(String[] args) {
        Application application = new Application();
        System.exit(application.start(args));
    }
}
