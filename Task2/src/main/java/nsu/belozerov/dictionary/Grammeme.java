package nsu.belozerov.dictionary;

@SuppressWarnings("SpellCheckingInspection")
public class Grammeme {
  private String name;
  private String parent;
  private String alias;
  private String description;

  public Grammeme() {
  }

  public Grammeme(String name, String parent, String alias, String description) {
    this.name = name;
    this.parent = parent;
    this.alias = alias;
    this.description = description;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getParent() {
    return this.parent;
  }

  public void setParent(String parent) {
    this.parent = parent;
  }

  public String getAlias() {
    return this.alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
