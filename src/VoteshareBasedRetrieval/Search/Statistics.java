package VoteshareBasedRetrieval.Search;

import VoteshareBasedRetrieval.Parser.TextReader;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by sajad on 11/25/2016.
 */
public class Statistics {
    private static ArrayList<String> experts;
    private static String query;
    private static String directory;
    private static ArrayList<String> real_rankings;

    public static void initExpertList(ArrayList<String> e) throws IOException {
        experts = e;
    }

    public static void setup(String q) throws IOException {
        real_rankings = TextReader.getExpertRealRanks(q);
//        VoteshareBasedRetrieval.Parser.TextReader.unsetRealRankedUserList();
//        query = q;
    }

    public static double percisionAt1(){
        if (real_rankings.contains(experts.get(0)))
            return 1;
        return 0;
    }

    public static double percisionAt5(){
        int relevant=0;
//        int j = Math.min(real_rankings.size(),5);
        int k = Math.min(5, experts.size());
        for (int i=0; i<k; i++){
            if (real_rankings.contains(experts.get(i))){
                relevant += 1;
            }
        }
        return (double) relevant/5;
    }

    public static double percisionAt10(){
        int relevant=0;
//        int size = real_rankings.size();
//        int j = Math.min(size, 10);
        int k = Math.min(experts.size(), 10);
        for (int i=0; i<k; i++){
            if (real_rankings.contains(experts.get(i))){
                relevant +=1;
            }
        }
        return (double) relevant/10;
    }
    public static double AP(){
        int relevant=0;
        double AvPrc =0;
        for (int i=0; i<experts.size(); i++){
            if (real_rankings.contains(experts.get(i))){
                relevant +=1;
                AvPrc += (double) relevant/(i+1);
            }
        }
        return AvPrc/(double)real_rankings.size();
    }
}
