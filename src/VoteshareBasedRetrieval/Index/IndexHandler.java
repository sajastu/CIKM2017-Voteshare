package VoteshareBasedRetrieval.Index; /**
 * Created by sajad on 10/19/2016.
 */
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class IndexHandler extends DefaultHandler {

    private HashMap<String, String> postIDs;
    private HashMap<String, String> vote_shares;
    private HashMap<String, String> vote_sharesPrime;
    private HashMap<String, String> answer_tags;

    private Indexer indxr;;
    private Answer answer;
    private String nIndxDirectory;


    public IndexHandler(HashMap<String, String> post_ids, HashMap<String, String> vShare, HashMap<String, String> answer_Tags, HashMap<String, String> vote_sharePrime) throws IOException {
        postIDs = new HashMap<>();
        vote_shares = new HashMap<>();
        vote_sharesPrime = new HashMap<>();
        answer_tags = new HashMap<>();
        postIDs = post_ids;
        vote_shares = vShare;
        vote_sharesPrime = vote_sharePrime;
        answer_tags = answer_Tags;
        nIndxDirectory = "C:\\paper_projects\\files";
        indxr = new Indexer("C:\\paper_projects\\indexes");
    }

    @Override
    public void startElement(String uri,
                             String localName, String qName, Attributes attributes)
            throws SAXException {
        if (qName.equalsIgnoreCase("row")) {
            if ( attributes.getValue("PostTypeId").equals("2") && postIDs.containsKey(attributes.getValue("Id")) ){
                String body = attributes.getValue("Body");
                String docId = attributes.getValue("Id");
                String oUserId = attributes.getValue("OwnerUserId");
                String score = attributes.getValue("Score");
                String q_id = attributes.getValue("ParentId");
                try {
                    if ( docId != null && oUserId != null) {
                        answer = new Answer();
                        answer.setBody(body);
                        answer.setDocId(docId);
//                        if(oUserId == null){
//                            answer.setOwnerUserId("N/A");
//                        }
//                        else {
                        answer.setOwnerUserId(oUserId);
//                        }
                        answer.setScore(score);
                        answer.setTagList(answer_tags.get(docId).split("#"));
                        answer.setQuestionId(q_id);
                        answer.setVoteShare(vote_shares.get(docId));
                        answer.setVoteShareWithTimespan(vote_sharesPrime.get(docId));
                        indxr.indexAnswers(answer);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}