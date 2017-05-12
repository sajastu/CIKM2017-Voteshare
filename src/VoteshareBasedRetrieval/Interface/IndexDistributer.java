package VoteshareBasedRetrieval.Interface;

import VoteshareBasedRetrieval.Index.IndexHandler;
import VoteshareBasedRetrieval.Index.Indexer;
import VoteshareBasedRetrieval.Parser.TextReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.HashMap;

public class IndexDistributer {
    private static HashMap<String, String> q_a = new HashMap<>();
    private static HashMap<String, String> vote_shares = new HashMap<>();
    private static HashMap<String, String> answer_Tags = new HashMap<>();
    private static HashMap<String, String> vote_sharePrime = new HashMap<>();

    public static void main(String[] args){
        try {
            q_a = TextReader.readProcess("in-files\\Q_A.txt", ",");
            vote_shares = TextReader.readProcess("in-files\\voteShare.txt", "\t");
            vote_sharePrime = TextReader.readProcess("in-files\\voteSharePrime.csv", ",");
            answer_Tags = TextReader.parseTags("in-files\\java_a_tag.txt", ",");
            File inputFile = new File("D:\\University\\Information Retrieval 2\\Hws\\Hw1\\files\\Posts.xml");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            IndexHandler handler = new IndexHandler(q_a, vote_shares, answer_Tags, vote_sharePrime);
            saxParser.parse(inputFile, handler);
            Indexer.closeIndex();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
