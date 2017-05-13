package VoteshareBasedRetrieval.Interface;

import VoteshareBasedRetrieval.Parser.TextReader;
import VoteshareBasedRetrieval.Parser.TextWriter;
import VoteshareBasedRetrieval.Search.VsAbstract;
//import VoteshareBasedRetrieval.Search.Balog1;
import VoteshareBasedRetrieval.Search.VsRunner;
import VoteshareBasedRetrieval.Search.Statistics;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by sajad on 11/23/2016.
 */
public class UserInterface {

    private static VsAbstract searchB;
    private static HashMap<String, Double> experts;
    private static ArrayList<String> queries;


    private static double precisionAt1;
    private static double precisionAt5;
    private static double precisionAt10;
    private static double AP;
    private static double MAP;
    private static double MP1;
    private static double MP5;
    private static double MP10;

    private static void setup(){
        MAP =0;
        queries = new ArrayList<>();
        experts = new HashMap<>();
        queries = TextReader.getQueries();
    }

    private static VsAbstract getInstance() throws IOException {       //VoteshareBasedRetrieval.Search.VoteshareBasedRetrieval.Search.VsAbstract factory method
        return new VsRunner();
    }

    public static void main(String[] args) throws Exception {
        UserInterface.setup();
        System.out.println("--------- Expert finding ------------");
        System.out.println("Choose the language model");
        System.out.println("1. VoteshareBasedRetrieva without Timespan");
        System.out.println("2. VoteshareBasedRetrieval with Timespan");
        Scanner scn1 = new Scanner(System.in);
        String inp1 = scn1.nextLine();
        searchB = getInstance();
        switch (inp1){
            case "1":
                balog2Lambda(searchB, false);
                break;
            case "2":
                balog2Lambda(searchB,true);
                break;
            default:
                break;
        }
    }

    private static void balog2Lambda(VsAbstract searchB, boolean timespan) throws Exception {
        TextReader.setupRankedList();
        double lambdas[] = {0.01};
                //, 0.02, 0.03, 0.04, 0.05, 0.06, 0.07, 0.08, 0.09, 0.1};
        double voteShares[]= {0.500};
        int scores[] = {1};
                //, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        for (double l : lambdas) {
            for (double vs : voteShares) {
                for (int s : scores) {
                    for (String q : queries) {
                        searchB.emRun(q, s, vs, l, timespan);
                    }
                }
            }
        }
//        Scanner cs = new Scanner(System.in);
//        String s = cs.nextLine();
//        searchB.parseUserAnswerCount();
        int[] transParams = {1, 2, 3, 4, 5};
        for (int transParam : transParams) {
            for (double vs : voteShares) {
                for (int score : scores) {
                    searchB.parseTranslationFile(transParam, score, vs, 0.01);
                    for (String query : queries) {
                        System.out.println("Processing query: " + query);
                        Statistics.setup(query);
                        experts = searchB.expertRetrieval(10000, 0.01, query);     //This module return result based on em translation method
                        if (experts.size() != 0) {
                            Statistics.initExpertList(new ArrayList<>(experts.keySet()));
                            precisionAt1 = Statistics.percisionAt1();
                            precisionAt5 = Statistics.percisionAt5();
                            precisionAt10 = Statistics.percisionAt10();
                            AP = Statistics.AP();
                            System.out.println("for lambda = " + 0.01 + ", Average Precision: " + AP);
                        } else if (experts.size() == 0) {
                            System.out.println("No expert found!!");
                        }
                        TextWriter.writeToCsvFile(10000, transParam, 0.01,score, vs, query, precisionAt1, precisionAt5, precisionAt10, AP);
                        MAP += AP;
                        MP1 += precisionAt1;
                        MP5 += precisionAt5;
                        MP10 += precisionAt10;
                        resetFactors();
                    }
                    MP1 = MP1 / (double) 100;
                    MP5 = MP5 / (double) 100;
                    MP10 = MP10 / (double) 100;
                    MAP = MAP / (double) 100;
                    System.out.println("for lambda = " + 0.01 + ", MAP: " + MAP);
                    TextWriter.writeStatisticsFile(10000,transParam, vs, score, MAP );
                    TextWriter.writeToCsvFile(0, transParam, 0.01,score,vs, "", MP1, MP5, MP10, MAP);
                    MP1 = 0;
                    MP5 = 0;
                    MP10 = 0;
                    MAP = 0;
                }
            }
        }
    }
    private static void balog2Manu(VsAbstract searchB, boolean fixedN) throws IOException, ParseException {

    }

    private static void resetFactors() {
        precisionAt1 = 0;
        precisionAt5 = 0;
        precisionAt10 = 0;
        AP = 0;
        experts.clear();
    }

//
//    private static void printList(HashMap<String, Double> experts) {
//        Iterator<Map.Entry<String, Double>> it = experts.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry pair = (Map.Entry)it.next();
//            System.out.println(pair.getKey() + " = " + pair.getValue());
//            it.remove(); // avoids a ConcurrentModificationException
//        }
//    }
}
