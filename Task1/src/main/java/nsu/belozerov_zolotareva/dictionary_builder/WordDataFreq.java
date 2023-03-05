package nsu.belozerov_zolotareva.dictionary_builder;

public class WordDataFreq {
    private Double entryCounter;
    private int lastTextNum;
    private Double inTextCounter;

    public WordDataFreq(int lastTextNum) {
        entryCounter = 1.0;
        this.lastTextNum = lastTextNum;
        inTextCounter = 1.0;
    }

    public Double getEntryCounter() {
        return entryCounter;
    }

    public void setEntryCounter(Double entryCounter) {
        this.entryCounter = entryCounter;
    }

    public int getLastTextNum() {
        return lastTextNum;
    }

    public void setLastTextNum(int lastTextNum) {
        this.lastTextNum = lastTextNum;
    }

    public Double getInTextCounter() {
        return inTextCounter;
    }

    public void setInTextCounter(Double inTextCounter) {
        this.inTextCounter = inTextCounter;
    }

    public void increaseInTextCounter(){
        inTextCounter++;
    }

    public void increaseEntryCounter(){
        entryCounter++;
    }
}
