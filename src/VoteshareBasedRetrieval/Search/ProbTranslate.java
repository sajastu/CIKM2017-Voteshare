package VoteshareBasedRetrieval.Search;

/**
 * Created by sajad on 12/21/2016.
 */
class ProbTranslate
{
    private String word;

    private double prob;

    public double getProb()
    {
        return prob;
    }

    public void setProb(double prob)
    {
        this.prob = prob;
    }


    public String getWord()
    {
        return word;
    }

    public void setWord(String word)
    {
        this.word = word;
    }

    public ProbTranslate(String word, double prob)
    {
        this.word = word;
        this.prob = prob;
    }
}