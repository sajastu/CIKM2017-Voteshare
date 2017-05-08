package VoteshareBasedRetrieval.Interface;

import VoteshareBasedRetrieval.Index.IndexHandler;
import VoteshareBasedRetrieval.Index.Indexer;
import VoteshareBasedRetrieval.Parser.TextReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.Date;
import java.util.HashMap;

public class IndexDistributer {
    private static HashMap<String, String> hs = new HashMap<>();
    private static HashMap<String, String> vote_shares = new HashMap<>();
    private static HashMap<String, String> answer_Tags = new HashMap<>();
    public static void main(String[] args){
        try {
            long lStartTime1 = new Date().getTime();
            hs = TextReader.read_process("D:\\University\\Information Retrieval 2\\Hws\\Hw1\\files\\Q_A.txt", ",");
            vote_shares = TextReader.read_process("D:\\University\\Information Retrieval 2\\Hws\\Hw2\\voteShare.txt", "\t");
            answer_Tags = TextReader.parseTags("D:\\University\\Information Retrieval 2\\Hws\\Hw2\\java_a_tag.txt", ",");
            long lEndTime1 = new Date().getTime();
            long difference1 = lEndTime1 - lStartTime1;
            System.out.println("Elapsed milliseconds1: " + difference1);
            File inputFile = new File("D:\\University\\Information Retrieval 2\\Hws\\Hw1\\files\\Posts.xml");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            long lStartTime2 = new Date().getTime();
            IndexHandler handler = new IndexHandler(hs, vote_shares, answer_Tags);
            long lEndTime2 = new Date().getTime();
            long difference2 = lEndTime2 - lStartTime2;
            System.out.println("Elapsed milliseconds2: " + difference2);

            long lStartTime = new Date().getTime();
            saxParser.parse(inputFile, handler);
            long lEndTime = new Date().getTime();
            long difference = lEndTime - lStartTime;
            Indexer.closeIndex();

            System.out.println("Elapsed milliseconds3: " + difference);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
