package VoteshareBasedRetrieval.Search;

import org.apache.lucene.document.Document;

import java.util.HashMap;

/**
 * Created by sajad on 11/30/2016.
 */
public class DocumentList {
    private HashMap<Document, Pair> userDocList;

    public DocumentList(){
        userDocList = new HashMap<>();
    }

    public HashMap<Document, Pair> getUserDocList() {
        return userDocList;
    }

    public void setUserDocList(HashMap<Document, Pair> userDocList) {
        this.userDocList = userDocList;
    }

    public void addDetails(Document doc, Pair pair){
        userDocList.put(doc, pair);
    }

    //
//    public VoteshareBasedRetrieval.Search.DocumentList(){
//
//    }


}
