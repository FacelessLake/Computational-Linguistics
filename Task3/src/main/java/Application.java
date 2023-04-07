import ngram.NGram;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

@SuppressWarnings("SpellCheckingInspection")
public class Application {
  private static final String CHARSET = "UTF-8";
  private static final String APPLICATION_NAME = "N-gram processor";
  private Options options;
  private final CommandLineParser parser;
  private final HelpFormatter formatter;
  private String frequencyDictionary;
  private String opencorporaDictionary;
  private String inputFile;
  private String outputFile;
  private double threshold;

  public Application() {
    buildOptions();
    this.parser = new DefaultParser();
    this.formatter = new HelpFormatter();
    this.frequencyDictionary = "src/main/resources/frequency_dict.txt";
    this.opencorporaDictionary = "src/main/resources/dict.opcorpora.xml";
    this.threshold = 0.5;
  }

  public int start(String[] args) {
    try {
      processCommands(this.parser.parse(this.options, args));
    } catch(ParseException e) {
      System.err.println("Invalid argument!");
      return -1;
    }
    return 0;
  }

  private void buildOptions() {
    Option extract = Option.builder("e")
        .longOpt("extract")
        .argName("inputFile> <outputFile")
        .hasArgs()
        .numberOfArgs(2)
        .desc("Extract N-grams from the specified corpus and save dictionary into given outputFile")
        .build();
    Option frequency = Option.builder("f")
        .longOpt("frequency")
        .argName("frequencyDictionary")
        .hasArg()
        .desc("Specify frequency dictionary file to use")
        .build();
    Option load = Option.builder("l")
        .longOpt("load")
        .argName("inputFile")
        .hasArg()
        .desc("Load N-grams from the specified dictionary")
        .build();
    Option opencorpora = Option.builder("o")
        .longOpt("opencorpora")
        .argName("opencorporaDictionary")
        .hasArg()
        .desc("Specify opencorpora dictionary to use")
        .build();
        Option threshold = Option.builder("t")
            .longOpt("threshold")
            .argName("threshold")
            .hasArg()
            .desc("Specify threshold for N-gram extraction")
            .build();
    Option help = new Option("h", "help", false, "Print help information");
    this.options = new Options();
    this.options.addOption(extract);
    this.options.addOption(frequency);
    this.options.addOption(load);
    this.options.addOption(opencorpora);
    this.options.addOption(threshold);
    this.options.addOption(help);
  }

  private void processCommands(CommandLine cmd) {
    if (cmd.hasOption("f") || cmd.hasOption("frequency")) {
      processFrequency(cmd);
    }
    if (cmd.hasOption("o") || cmd.hasOption("opencorpora")) {
      processOpencorpora(cmd);
    }
    if (cmd.hasOption("t") || cmd.hasOption("threshOld")) {
      processThreshold(cmd);
    }
    if (cmd.hasOption("e") || cmd.hasOption("extract")) {
      processExtract(cmd);
    } else if (cmd.hasOption("l") || cmd.hasOption("load")) {
      processLoad(cmd);
    } else if (cmd.hasOption("h") || cmd.hasOption("help")) {
      processHelp();
    } else processUsage();
  }

  private void processFrequency(CommandLine cmd) {
    this.frequencyDictionary = cmd.getOptionValue("frequency");
  }

  private void processOpencorpora(CommandLine cmd) {
    this.opencorporaDictionary = cmd.getOptionValue("opencorpora");
  }

  private void processThreshold(CommandLine cmd) {
    this.threshold = Double.parseDouble(cmd.getOptionValue("threshold"));
  }

  private void processExtract(CommandLine cmd) {
    String[] values = cmd.getOptionValues("extract");
    this.inputFile = values[0];
    this.outputFile = values[1];
    System.out.println("Extracting N-grams from " + this.inputFile + "...");
    ArticleParser articleParser = new ArticleParser(this.frequencyDictionary, this.opencorporaDictionary, this.threshold);
    List<NGram> nGrams = articleParser.parse(this.inputFile);
    System.out.println("Successfully extracted " + nGrams.size() + " N-grams");
    System.out.println("Saving N-grams to " + this.outputFile + "...");
    System.out.println("Done!");
  }

  private void processLoad(CommandLine cmd) {
    this.inputFile = cmd.getOptionValue("load");
    System.out.println("Loading N-grams from " + this.inputFile + "...");
    processQueries();
  }

  private void processHelp() {
    this.formatter.printHelp(APPLICATION_NAME, this.options);
  }

  private void processUsage() {
    try (PrintWriter writer = new PrintWriter(System.out, true)) {
      this.formatter.printUsage(writer, 80, APPLICATION_NAME, this.options);
    }
  }

  private void processQueries() {
    Scanner input = new Scanner(System.in, CHARSET);
    System.out.println("Choose query type or enter 'exit' to quit:");
    System.out.println("1) Search N-gram by word");
    System.out.println("2) Retrieve nested N-grams");
    while (!input.hasNext("[Ee]xit")) {
      try {
        int queryType = Integer.parseInt(input.nextLine());
        if (queryType == 1) {
          System.out.println("Enter word to search for:");
          String query = input.nextLine();
        } else if (queryType == 2) {
          System.out.println("Enter N-gram to search for:");
          String query = input.nextLine();
        } else throw new NumberFormatException();
      } catch (NumberFormatException e) {
        System.out.println("Invalid input!");
      } finally {
        System.out.println("\nChoose query type or enter 'exit' to quit:");
        System.out.println("1) Search N-gram by word");
        System.out.println("2) Retrieve nested N-grams");
      }
    }
    System.out.println("Exiting...");
  }
}
