package VoteshareBasedRetrieval.Parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by sajad on 10/19/2016.
 */
public class TextReader {

    public static ArrayList<String> realRankedUsers;

    private static HashMap<String, String> postIDs = new HashMap<>();
    private static HashMap<String, String> vote_shares = new HashMap<>();
    private static HashMap<String, String> answer_Tags = new HashMap<>();

    public static void reader(String filePath, String del) {
        int l = 2;
        if (Objects.equals(del, ","))
            l = 3;

        BufferedReader br = null;
        try {
            String aId;
            String[] aArr;
            br = new BufferedReader(new FileReader(filePath));
            aId = br.readLine();
            while( (aId != null) && (aId.split(del).length == l) && (!aId.isEmpty())){
                aArr = aId.split(del);
                if (Objects.equals(del, ","))
                    postIDs.put(aArr[1], aArr[0]);
                else
                    vote_shares.put(aArr[0],aArr[1]);
                aId = br.readLine();
                }
            } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static void setupRankedList(){
        realRankedUsers = new ArrayList<>();
    }

    public static HashMap<String, String> read_process(String directory, String del){
        TextReader.reader(directory, del);
        if (Objects.equals(del, ","))
            return postIDs;
        else
            return vote_shares;
    }

    public static HashMap<String, String> parseTags(String directory, String del){
        TextReader.tagsReader(directory, del);
        return answer_Tags;
    }

    private static void tagsReader(String directory, String del) {
        BufferedReader br = null;
        try {
            String aId;
            String[] aArr;
            br = new BufferedReader(new FileReader(directory));
            aId = br.readLine();
            while( (aId != null) && (aId.split(del).length == 2) && (!aId.isEmpty())){
                aArr = aId.split(del);
                if (answer_Tags.containsKey(aArr[0])){
                    String a = answer_Tags.get(aArr[0]);
                    a = a.concat("#"+aArr[1]);
                    answer_Tags.put(aArr[0], a);
                }
                else
                    answer_Tags.put(aArr[0], aArr[1]);
                aId = br.readLine();
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    public static ArrayList<String> getQueries() {
        BufferedReader br = null;
        String directory="/home/lab/Desktop/TopicModelingRetrieval/src/Files/queries.txt";
        ArrayList<String> queries = new ArrayList<String>();
        try {
            String query;
            br = new BufferedReader(new FileReader(directory));
            query = br.readLine();
            while (query != null) {
                queries.add(query);
                query = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return queries;
    }

    public static ArrayList<String> getExpertRealRanks(String query) throws IOException {
        realRankedUsers.clear();
        String directory = "/home/lab/Desktop/EMRetrieval/DataSet/DataSetFor"+query.toLowerCase()+".csv";
        BufferedReader br = null;
        try {
            String line;
            br = new BufferedReader(new FileReader(directory));
            line = br.readLine();
            line = br.readLine();
            while (line != null) {
                realRankedUsers.add(line.split(",")[0]);
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return realRankedUsers;
    }

    public static HashMap<String, Double> parseUserAnswerCount() {
        HashMap<String , Double> user_answer_count = new HashMap<>();
        String directory = "D:\\University\\Information Retrieval 2\\Hws\\Hw1\\files\\user_answer_count.txt";
        BufferedReader br = null;
        try {
            String line;
            br = new BufferedReader(new FileReader(directory));
            line = br.readLine();
            while (line != null) {
                String[] record = line.split("\t");
                user_answer_count.put(record[0], Double.parseDouble(record[1]));
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user_answer_count;
    }

//    public static void unsetRealRankedUserList() {
//        realRankedUsers = null;
//    }
}
