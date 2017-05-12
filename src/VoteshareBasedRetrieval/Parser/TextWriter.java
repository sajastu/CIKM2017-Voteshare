package VoteshareBasedRetrieval.Parser;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sajad on 11/27/2016.
 */
public class TextWriter {
    static String csvFile;
    static String txtFile;
    public static <T> void writeToCsvFile(int n, int transParam, T beta, int score,double voteShare, String query, double precisionAt1, double precisionAt5, double precisionAt10, double map) throws IOException {
        csvFile = "/home/lab/Desktop/EMRetrieval/FinalResults/S" + score + "L" + beta + "VS" + voteShare +"T" + transParam + ".csv";
        FileWriter writer = new FileWriter(csvFile,true);

        writeLine(writer, Arrays.asList(String.valueOf(n), String.valueOf(beta), query, String.valueOf(map), String.valueOf(precisionAt1),
                                        String.valueOf(precisionAt5), String.valueOf(precisionAt10)));
        writer.flush();
        writer.close();
    }

    public static void writeToTxtFile(StringBuilder sb, double voteShare, double lambda, int score) throws IOException {
        txtFile = "o-file\\translations\\S"+score+"L"+lambda+"V"+voteShare+".txt";
        FileWriter wrt = new FileWriter(txtFile,true);
        sb.setLength(sb.length() - 1);
        wrt.append(sb.toString());
        wrt.flush();
        wrt.close();
    }

    public static void writeStatisticsFile(int n, int transParam, double vs, int score, double MAP) throws IOException {
        csvFile = "/home/lab/Desktop/EMRetrieval/stats.csv";
        FileWriter writer = new FileWriter(csvFile,true);
        writeLine(writer, Arrays.asList(String.valueOf(n),String.valueOf(transParam),String.valueOf(vs),String.valueOf(score),String.valueOf(MAP)));
        writer.flush();
        writer.close();
    }

    public static void writeLine(Writer w, List<String> values) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            sb.append(value + ",");
        }
        sb.setLength(sb.length() - 1);
        sb.append("\n");
        w.append(sb.toString());
    }

    public static void writeFileHeader() throws IOException {
        FileWriter writer = new FileWriter(csvFile,true);
        writeLine(writer, Arrays.asList("N","Lambda","Query","MAP","p@1","p@5","p@10"));
        writer.flush();
        writer.close();

    }

    public static void writeToCsv(String trans, Double aDouble, Double aDouble1) throws IOException {
        csvFile = "/home/lab/Desktop/android.csv";
        FileWriter writer = new FileWriter(csvFile,true);

        writeLine(writer, Arrays.asList(trans, String.valueOf(aDouble), String.valueOf(aDouble1)));
        writer.flush();
        writer.close();
    }
}
