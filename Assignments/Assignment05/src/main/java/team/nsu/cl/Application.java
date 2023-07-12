package team.nsu.cl;

import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Scanner;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import team.nsu.cl.dictionary.OpencorporaDictionary;
import team.nsu.cl.dictionary.frequency.FrequencyDictionaryParser;
import team.nsu.cl.index.Corpus;
import team.nsu.cl.thesaurus.ThesaurusParser;

@SuppressWarnings("SpellCheckingInspection")
public class Application {
  private static final Charset CHARSET = System.console().charset();
  private static final String APPLICATION_NAME = "Search engine";
  private final Options options;
  private final CommandLineParser parser;
  private final HelpFormatter formatter;
  private String corpus;
  private String frequencyDictionary;
  private String opencorporaDictionary;
  private String thesaurus;

  public Application() {
    this.options = new Options();
    buildOptions();
    this.parser = new DefaultParser();
    this.formatter = new HelpFormatter();
    this.corpus = "../../Resources/corpus.xml";
    this.frequencyDictionary = "../../Resources/frequency_dict.txt";
    this.opencorporaDictionary = "../../Resources/dict.opcorpora.xml";
    this.thesaurus = "../../Resources/thesaurus.json";
  }

  public int start(String[] args) {
    try {
      processCommands(this.parser.parse(this.options, args));
      FrequencyDictionaryParser frequencyDictionaryParser = new FrequencyDictionaryParser();
      frequencyDictionaryParser.parse(this.frequencyDictionary);
      OpencorporaDictionary dictionary = OpencorporaDictionary.load(this.opencorporaDictionary);
      ArticleParser articleParser = new ArticleParser(frequencyDictionaryParser, dictionary);
      Corpus textCorpus = articleParser.parse(this.corpus);
      ThesaurusParser thesaurusParser = new ThesaurusParser();
      SearchEngine searchEngine = new SearchEngine(thesaurusParser.parseThesaurus(this.thesaurus), textCorpus, dictionary);
      processQueries(searchEngine);
    } catch(ParseException e) {
      System.err.println("Invalid argument!");
      return -1;
    }
    return 0;
  }

  private void buildOptions() {
    Option corpus = Option.builder("c")
        .longOpt("corpus")
        .argName("corpus")
        .hasArg()
        .desc("Specify corpus file to use")
        .build();
    Option frequency = Option.builder("f")
        .longOpt("frequency")
        .argName("frequencyDictionary")
        .hasArg()
        .desc("Specify frequency dictionary file to use")
        .build();
    Option opencorpora = Option.builder("o")
        .longOpt("opencorpora")
        .argName("opencorporaDictionary")
        .hasArg()
        .desc("Specify opencorpora dictionary to use")
        .build();
    Option thesaurus = Option.builder("t")
        .longOpt("thesaurus")
        .argName("thesaurus")
        .hasArg()
        .desc("Specify thesaurus file to use")
        .build();
    Option help = new Option("h", "help", false, "Print help information");
    this.options.addOption(corpus);
    this.options.addOption(frequency);
    this.options.addOption(opencorpora);
    this.options.addOption(thesaurus);
    this.options.addOption(help);
  }

  private void processCommands(CommandLine cmd) {
    if (cmd.hasOption("c") || cmd.hasOption("corpus")) {
      processCorpus(cmd);
    }
    if (cmd.hasOption("f") || cmd.hasOption("frequency")) {
      processFrequency(cmd);
    }
    if (cmd.hasOption("o") || cmd.hasOption("opencorpora")) {
      processOpencorpora(cmd);
    }
    if (cmd.hasOption("t") || cmd.hasOption("thesaurus")) {
      processThesaurus(cmd);
    }
    if (cmd.hasOption("h") || cmd.hasOption("help")) {
      processHelp();
    }
  }

  private void processCorpus(CommandLine cmd) {
    this.corpus = cmd.getOptionValue("corpus");
  }

  private void processFrequency(CommandLine cmd) {
    this.frequencyDictionary = cmd.getOptionValue("frequency");
  }

  private void processOpencorpora(CommandLine cmd) {
    this.opencorporaDictionary = cmd.getOptionValue("opencorpora");
  }

  private void processThesaurus(CommandLine cmd) {
    this.thesaurus = cmd.getOptionValue("thesaurus");
  }

  private void processHelp() {
    this.formatter.printHelp(APPLICATION_NAME, this.options);
  }

  private void processQueries(SearchEngine searchEngine) {
    Scanner input = new Scanner(System.in, CHARSET);
    System.out.println("Search query:");
    while (!input.hasNext("[Ee]xit")) {
      String query = input.nextLine();
      searchEngine.search(query);
      System.out.println("Search query:");
    }
    System.out.println("Exiting...");
  }
}
