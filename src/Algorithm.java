import java.io.FileNotFoundException;
import java.io.IOException;

public interface Algorithm {
    String getAlgorithmName();
    void compress(String inputPath, String outputPath) throws IOException;
    void decompress(String inputPath, String outputPath) throws IOException;
}
