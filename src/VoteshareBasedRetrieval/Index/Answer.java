package VoteshareBasedRetrieval.Index;

/**
 * Created by sajad on 10/19/2016.
 */
public class Answer {
    private String score;
    private String body;
    private String ParentId;
    private String ownerUserId;
    private String questionId;
    private String docId;
    private String voteShare;
    private String voteShareWithTimespan;
    private String[] tagList;

    public String getVoteShareWithTimespan() {
        return voteShareWithTimespan;
    }

    public void setVoteShareWithTimespan(String voteShareWithTimespan) {
        this.voteShareWithTimespan = voteShareWithTimespan;
    }

    public String[] getTagList() {
        return tagList;
    }

    public void setTagList(String[] tagList) {
        this.tagList = tagList;
    }

    public String getVoteShare() {
        return voteShare;
    }

    public void setVoteShare(String voteShare) {
        this.voteShare = voteShare;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(String ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getParentId() {
        return ParentId;
    }

    public void setParentId(String parentId) {
        ParentId = parentId;
    }
}
