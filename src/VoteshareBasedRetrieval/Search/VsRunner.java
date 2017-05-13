package VoteshareBasedRetrieval.Search;

import VoteshareBasedRetrieval.Parser.TextWriter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.TermStats;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by sajad on 11/23/2016.
 */

public class VsRunner extends VsAbstract {

    private RAMDirectory ramDirHv;
    private IndexWriter ramWriterHv;
    private RAMDirectory ramDirCollection;
    private IndexWriter ramWriterCollection;
    private HashMap<String, Double> probHv;
    private HashMap<String, Long> freqHv;
    private HashMap<String, Double> probTermsInCollectionList;
    private HashMap<String, Double> et;
    private ArrayList<String> html_tags;

    public VsRunner() throws IOException {
        iReader =  DirectoryReader.open(FSDirectory.open(Paths.get("/home/lab/Desktop/Untitled Folder/indexes/answers")));
        iSearcher = new IndexSearcher(iReader);
        experts_count = new HashMap<>();
        experts = new HashMap<>();
        documentsList = new HashMap<>();
        userQueryTerms = new ArrayList<>();
        eqQueries = new ArrayList<>();
        user_answer_count = new HashMap<>();
        highVote = new HashMap<>();
        lowVote = new HashMap<>();
        et = new HashMap<>();
        html_tags = new ArrayList<>();
        if (html_tags.size() == 0){
            html_tags.addAll(Arrays.asList("lt","gt","hr","p","code","em","h1","h2","h3","a","b","blockquote","body","div","img","kbd","li","pre","small","ul","br"));
        }
        tf = new HashMap<>();
        et_probability = new HashMap<>();
        words_col_prob = new HashMap<>();

    }

    @Override
    public HashMap<String, Double> retriveExperts(int n,int beta, String query) throws ParseException, IOException {

        return sortHashMapByValues(experts);
    }

    @Override
    public double pOfDocModel(double probOfQueryTerm, double probOfDoc, double lambda) {
        return 0;
    }

    @Override
    public HashMap<String, Double> expertRetrieval(int n, double lambda, String query) throws IOException, ParseException {
        deletePreviousEntities();
        userQuery = query;
        documentsList = luceneDefaultExpertRetrieve2(n);
        for (Document doc : documentsList.keySet()) {
            String uID = doc.getField("userId").stringValue();
            Double Score = experts.get(uID);
            if (Score == null) {
                experts.put(uID, 1.0);
            } else
                experts.put(uID, Score + 1.0);
        }
        return sortHashMapByValues(experts);
    }

    @Override
    public void emRun(String query, int score, double voteShare, double lambda, boolean timespan) throws Exception {
        deletePreviousEntities();
        userQuery = query;
        this.lambda = lambda;
        luceneDefaultExpertRetrieve(10000, score, voteShare, true, timespan);
        luceneDefaultExpertRetrieve(10000, score, voteShare, false, timespan);

        ramDirHv = new RAMDirectory();
        ramWriterHv = new IndexWriter(ramDirHv,new IndexWriterConfig(new StandardAnalyzer()));
        ramDirCollection = new RAMDirectory();
        ramWriterCollection = new IndexWriter(ramDirCollection,new IndexWriterConfig(new StandardAnalyzer()));

        for (Document doc : highVote.keySet()){
            ramWriterHv.addDocument(doc);
            ramWriterCollection.addDocument(doc);
        }

        for (Document doc : lowVote.keySet()){
            ramWriterCollection.addDocument(doc);
        }

        System.out.println(userQuery + ", Highly Voted: " + ramWriterHv.maxDoc());
        System.out.println(userQuery + ", Low Voted: " + (ramWriterCollection.maxDoc() - ramWriterHv.maxDoc()));

        IndexReader reader =  DirectoryReader.open(ramWriterHv);

        org.apache.lucene.misc.TermStats[] commonTerms = HighFreqTerms.getHighFreqTerms(reader, 15000, "Body", new Comparator<org.apache.lucene.misc.TermStats>() {
            @Override
            public int compare(org.apache.lucene.misc.TermStats o1, org.apache.lucene.misc.TermStats o2) {
                if(o1.totalTermFreq > o2.totalTermFreq) {
                    return 1;
                } else if(o2.totalTermFreq > o1.totalTermFreq) {
                    return -1;
                }
                return 0;
            }
        });

        IndexReader readerAll =  DirectoryReader.open(ramWriterCollection);

        probHv = new HashMap<String, Double>();
        freqHv = new HashMap<String, Long>();
        probTermsInCollectionList = new HashMap<String, Double>();

        ArrayList<org.apache.lucene.misc.TermStats> commonTermArray = new ArrayList<>();

        for (org.apache.lucene.misc.TermStats commonTerm : commonTerms){
            String term = commonTerm.termtext.utf8ToString();
            if (getDocFreq(term,reader) > 1 && preProcessTheTerm(term)){
                commonTermArray.add(commonTerm);
            }
        }

        for (org.apache.lucene.misc.TermStats commonTerm : commonTermArray) {
            String term = commonTerm.termtext.utf8ToString();
            String key = userQuery+"#"+term;
            double prob = getTermProbability(term, reader);
            double collectionTermProb = getTermProbability(term, readerAll);
            long termFreq = getTermCount(term, reader);
            probHv.put(key, prob);
            freqHv.put(key, termFreq);
            probTermsInCollectionList.put(key, collectionTermProb);
        }

        probTermsInCollectionList = sortHashMapByValues(probTermsInCollectionList);

        for (int i=1 ; i<100; i++){
            e(commonTermArray);
            double eLength = 0;
            for (org.apache.lucene.misc.TermStats commonTerm : commonTermArray) {
                String term = commonTerm.termtext.utf8ToString();
                String key = userQuery+"#"+term;
                eLength += et.get(key);
            }
            m(commonTermArray,eLength);
        }
        StringBuilder sb = extractTop10(sortHashMapByValues(probHv));
        probHv = sortHashMapByValues(probHv);
        TextWriter.writeToTxtFile(sb,voteShare,lambda,score);
        System.out.println("Query: " + userQuery + " has been translated successfully!");
        writeToExel();
    }

    private boolean preProcessTheTerm(String term) {
        if (term.matches("-?\\d+(\\.\\d+)?") || html_tags.contains(term)){
            return false;
        }
        return true;
    }

    private int getDocFreq(String term, IndexReader reader) throws IOException {
        Term termInstance = new Term("Body", term);
        return reader.docFreq(termInstance);
    }

    private StringBuilder extractTop10(LinkedHashMap<String, Double> stringDoubleLinkedHashMap) {
        StringBuilder sb = new StringBuilder();
        DecimalFormat format = new DecimalFormat("0.00000");
        int counter = 0;
        sb.append(userQuery+"~");
        for (Iterator<String> iterator = stringDoubleLinkedHashMap.keySet().iterator(); iterator.hasNext(); ) {
            String key = iterator.next();
            double val = stringDoubleLinkedHashMap.get(key);
            if (counter == 9){
                sb.append(key.split("#")[1]).append("|e|").append(format.format(val));
                break;
            }
            else
                sb.append(key.split("#")[1]).append("|e|").append(format.format(val)).append(" |o| ");
            counter++;
        }
        sb.append("\r\n");
        return sb;
    }

    private void writeToExel() throws IOException {
        for (String key : probHv.keySet()){
            String trans = key.split("#")[1];
            TextWriter.writeToCsv(trans, probHv.get(key), probTermsInCollectionList.get(key));
        }
    }

    private void m(ArrayList<org.apache.lucene.misc.TermStats> commonTerms, double eLength) {
        for (org.apache.lucene.misc.TermStats commonTerm : commonTerms) {
            String term = commonTerm.termtext.utf8ToString();
            String key = userQuery+"#"+term;
            probHv.put(key, et.get(key) / eLength);
        }
    }

    private void e(ArrayList<org.apache.lucene.misc.TermStats> commonTerms) {
        for (org.apache.lucene.misc.TermStats commonTerm : commonTerms) {
            String term = commonTerm.termtext.utf8ToString();
            String key = userQuery+"#"+term;
            long tf = freqHv.get(key);
            double probHvTerm = probHv.get(key) * this.lambda;
            double probAll = probTermsInCollectionList.get(key) * (1.0 - this.lambda);
            et.put(key, (tf * probHvTerm) / (probAll + probHvTerm));
        }
    }

    Comparator<TermStats> comparator = new Comparator<TermStats>() {
        @Override
        public int compare(TermStats o1, TermStats o2) {
            if(o1.totalTermFreq > o2.totalTermFreq) {
                return 1;
            } else if(o2.totalTermFreq > o1.totalTermFreq) {
                return -1;
            }
            return 0;
        }
    };

    private void deletePreviousEntities() throws IOException {
        if (ramWriterHv != null) {
            ramWriterHv.deleteAll();
            ramWriterCollection.deleteAll();
            highVote.clear();
            lowVote.clear();
            freqHv.clear();
            probHv.clear();
            probTermsInCollectionList.clear();
            et.clear();
        }
        experts_count.clear();
        experts.clear();
        eqQueries.clear();
        documentsList.clear();
    }

}