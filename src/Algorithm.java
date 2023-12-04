import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public interface Algorithm {
    String getAlgorithmName();
    ArrayList<String> getRequiredData();
    void compress(String inputPath, String outputPath, HashMap<String,Integer> required) throws IOException;
    void decompress(String inputPath, String outputPath, HashMap<String,Integer> required) throws IOException;
}
