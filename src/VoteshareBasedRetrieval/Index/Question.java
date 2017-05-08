package VoteshareBasedRetrieval.Index;

/**
 * Created by sajad on 12/25/2016.
 */
public class Question {
    private String qId;

    public String getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(String answerCount) {
        this.answerCount = answerCount;
    }

    private String answerCount;

    public String getqId() {
        return qId;
    }

    public void setqId(String qId) {
        this.qId = qId;
    }
}
