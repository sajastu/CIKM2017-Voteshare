package VoteshareBasedRetrieval.Search;

/**
 * Created by sajad on 11/26/2016.
 */
public class Pair {
    private double probability;
    private int docLength;

    public double getProbability() {
        return probability;
    }

    public int getDocLength() {
        return docLength;
    }

    public Pair(double probability, int length){
        this.docLength = length;
        this.probability = probability;
    }
}
