package VoteshareBasedRetrieval.Index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by sajad on 10/19/2016.
 */
public class Indexer {
    public static IndexWriter qWriter;
    public static IndexWriter aWriter;
    private int score;

    public Indexer(String dir) throws IOException {
        Analyzer analyzer = new StandardAnalyzer();
        Directory indexDirA = FSDirectory.open(Paths.get(dir+"\\answers"));
//        Directory indexDirQ = FSDirectory.open(Paths.get(dir+"\\questions"));
        IndexWriterConfig cfgA = new IndexWriterConfig(analyzer);
        cfgA.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
//        counter = 0;
        aWriter = new IndexWriter(indexDirA,cfgA);
//        qWriter = new IndexWriter(indexDirQ,cfgA);
    }

    public static void closeIndex() throws IOException {
        aWriter.close();
//        qWriter.close();
    }
    private Document getAnswerDocument(Answer answer){
        Document doc = new Document();
        doc.add(new StringField("userId",answer.getOwnerUserId(), Field.Store.YES ));
        doc.add(new StringField("docId",answer.getDocId(), Field.Store.YES ));
        doc.add(new StringField("questionId", answer.getQuestionId(), Field.Store.YES));
        doc.add(new StringField("voteShare", answer.getVoteShare(), Field.Store.YES));
        doc.add(new StringField("voteSharePrime", answer.getVoteShareWithTimespan(), Field.Store.YES));
//        doc.add(new StringField("voteShareWithTimespan", answer.getVoteShare(), Field.Store.YES));
        FieldType fType = new FieldType();
        fType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
        fType.setStored(true);
        fType.setStoreTermVectors(true);
        fType.setTokenized(true);
        Field ans = new Field("Body", answer.getBody(), fType);
        doc.add(ans);
        score = Integer.parseInt(answer.getScore());
        doc.add(new IntPoint("score", score ));
        doc.add(new DoublePoint ("voteShare", Double.parseDouble(answer.getVoteShare())));
        doc.add(new DoublePoint ("voteSharePrime", Double.parseDouble(answer.getVoteShareWithTimespan())));
//        doc.add(new DoublePoint ("voteShareWithTimespan", Double.parseDouble(answer.getVoteShare())));
        for (String tag : answer.getTagList())
            doc.add(new TextField("Tags", tag,Field.Store.YES));
        return doc;
    }

    public void indexAnswers(Answer answer) throws IOException {
        Document doc = getAnswerDocument(answer);
        aWriter.addDocument(doc);
    }
}
