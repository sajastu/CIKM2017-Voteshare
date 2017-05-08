package VoteshareBasedRetrieval.Search;

import VoteshareBasedRetrieval.Parser.TextReader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

//import org.apache.lucene.search.similarities.DefaultSimilarity;

/**
 * Created by sajad on 11/25/2016.
 */
public abstract class VsAbstract {
    int beta;
    double PmleDoc;
    double lambda;
    double userPoint;
    int docLength;
    ArrayList<String> userQueryTerms;
    String userQuery;
    ArrayList<String> eqQueries;

    protected IndexReader iReader;
    protected IndexSearcher iSearcher;

    protected HashMap<Document, Terms> documentsList;
    protected HashMap<String, Double> experts;
    protected HashMap<String, Double> user_answer_count;
    protected HashMap<String, Integer> experts_count;
    protected HashMap<String,ArrayList<ProbTranslate>> tags;
    protected HashMap<String, Integer> tf;
    protected HashMap<String, Double> et_probability;
    protected HashMap<String, Double> words_col_prob;

    protected HashMap<Document, Terms> highVote;
    protected HashMap<Document, Terms> lowVote;

    public abstract HashMap<String, Double> retriveExperts(int n,int beta, String query) throws ParseException, IOException;
    public abstract double pOfDocModel(double probOfQueryTerm, double probOfDoc, double lambda);
    public abstract HashMap<String, Double> expertRetrieval(int n, double lambda, String query) throws IOException, ParseException;

    public void parseQuery(String query){
        userQuery = query.replace("-"," ");
        userQueryTerms.clear();
        Collections.addAll(userQueryTerms, userQuery.split(" "));
    }

    public HashMap<Document, Terms> luceneDefaultExpertRetrieve2(int n) throws ParseException, IOException {
        documentsList.clear();
        QueryParser parser = new QueryParser("Body", new StandardAnalyzer());
        QueryParser parser1 = new QueryParser("Tags", new StandardAnalyzer());
        BooleanQuery.Builder bq = new BooleanQuery.Builder();

        Query o_query = parser.parse(userQuery);
        bq.add(o_query,BooleanClause.Occur.SHOULD);
        for (ProbTranslate tran : tags.get(userQuery)) {
            if (!Objects.equals(tran.getWord(), "")) {
                BooleanQuery.Builder bq1 = new BooleanQuery.Builder();
                Query query = parser1.parse(userQuery);
                Query query1 = parser.parse(tran.getWord());
                bq1.add(query, BooleanClause.Occur.MUST);
                bq1.add(query1, BooleanClause.Occur.MUST);
                bq.add(bq1.build(), BooleanClause.Occur.SHOULD);
            }
        }
        TopDocs hits = iSearcher.search(bq.build(),n * tags.get(userQuery).size());
        for (int i=0; i<hits.scoreDocs.length; i++){
            Terms termVector = iSearcher.getIndexReader().getTermVector(hits.scoreDocs[i].doc, "Body");
            Document doc = iSearcher.doc(hits.scoreDocs[i].doc);
            documentsList.put(doc, termVector);
        }
        return documentsList;
    }

    private String getAnalyzed(String term) {
        Analyzer analyzer = new StandardAnalyzer();
        ArrayList<String> queryTokens = new ArrayList<>();
        TokenStream tstream = null;
        try {
            String out = "";

            tstream = analyzer.tokenStream(null, term);
            tstream.reset();
            while (tstream.incrementToken()) //for term t in query
            {
                out = (tstream.getAttribute(CharTermAttribute.class).toString());
            }
            tstream.close();
            return out;


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }



    public void luceneDefaultExpertRetrieve(int n, int score, double voteShare, boolean hv) throws ParseException, IOException {
//        documentsList.clear();
        QueryParser parser = new QueryParser("Tags", new StandardAnalyzer());
        Query query = parser.parse(userQuery);
        Double upper, lower;
        Integer lowerScore = 0;
        if (hv) {
            upper = 1.00;
            lower = voteShare;
            lowerScore = score;
        }
        else {
            upper = voteShare;
            lower = 0.00;
        }
        Query query1 = DoublePoint.newRangeQuery("voteShare", lower, upper);

        BooleanQuery.Builder bq = new BooleanQuery.Builder();
        bq.add(query, BooleanClause.Occur.MUST);
        bq.add(query1, BooleanClause.Occur.MUST);
        if (hv) {
            Query query2 = IntPoint.newRangeQuery("score", lowerScore, Integer.MAX_VALUE);
            bq.add(query2, BooleanClause.Occur.MUST);
        }
        TopDocs hits = iSearcher.search(bq.build(), n);
        for (int i=0; i<hits.scoreDocs.length; i++){
            Terms termVector = iSearcher.getIndexReader().getTermVector(hits.scoreDocs[i].doc, "Body");
            Document doc = iSearcher.doc(hits.scoreDocs[i].doc);
            if (hv)
                highVote.put(doc, termVector);
            else
                lowVote.put(doc,termVector);
        }
    }

    public double getTermProbability(String query, IndexReader reader) throws IOException {      //p(t)
        Term termInstance = new Term("Body", query);
        long termFreq = reader.totalTermFreq(termInstance);
        long coll_length = reader.getSumTotalTermFreq("Body");
        return ((double)termFreq/coll_length);
    }

    public long getTermCount(String query, IndexReader reader) throws IOException {      //p(t)
        Term termInstance = new Term("Body", query);
        return reader.totalTermFreq(termInstance);
    }

    public LinkedHashMap<String, Double> sortHashMapByValues(HashMap<String, Double> passedMap) {
        List<String> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Double> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);
        Collections.reverse(mapValues);

        LinkedHashMap<String, Double> sortedMap =
                new LinkedHashMap<>();

        for (double val : mapValues) {
            Iterator<String> keyIt = mapKeys.iterator();
            while (keyIt.hasNext()) {
                String key = keyIt.next();
                Double comp1 = passedMap.get(key);
                Double comp2 = val;
                if (comp1.equals(comp2)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }

    public void parseUserAnswerCount() {
        user_answer_count = TextReader.parseUserAnswerCount();
    }

    public void parseTranslationFile(int transParam, int score, double voteShare, double lambda) throws IOException {
        String path = "/home/lab/Desktop/EMRetrieval/results/S"+score+"L"+lambda+"V"+voteShare+".txt";
        Path filePath = new File(path).toPath();
//        Charset charset = Charset.forName("UTF-8");
        List<String> stringList = Files.readAllLines(filePath);
        tags = new HashMap<>();
        tags.clear();
        for (String s : stringList)
        {
            String[] tgs = s.split("~");
            ArrayList<ProbTranslate> e = new ArrayList<>();
            if(tgs.length > 1 && tgs[1] != null && !Objects.equals(tgs[1], "") )
            {

                String [] trs = tgs[1].split("\\s?\\|[o]\\|\\s?");
//                String [] trs = tgs[1].split(",");

//                e.add(new ProbTranslate(tgs[0],0.10));

                for (int i=0 ; i<transParam; i++) {
                    String t = trs[i].split("\\|[e]\\|")[0];
//                    String t = trs[i].split(":")[0];
//                    if (t.length()!=0 && !Objects.equals(t, userQuery)) {
                    try {
                        e.add(new ProbTranslate(t, Double.parseDouble(trs[i].split("\\|[e]\\|")[1])));
                    }
                    catch (Exception e1){
                        e1.printStackTrace();
                    }
//                    }
                }
            }
            else
            {
                e.add(new ProbTranslate(tgs[0], 1));
            }
            tags.put(tgs[0], e );
        }
    }

    public abstract void emRun(String query, int score, double voteShare, double lambda, boolean timespan) throws Exception;
}
