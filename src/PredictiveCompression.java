import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class PredictiveCompression implements Algorithm{
    @Override
    public String getAlgorithmName() {
        return "2D Predictive Compression";
    }

    @Override
    public ArrayList<String> getRequiredData() {
        ArrayList<String> required = new ArrayList<>();
        required.add("start");
        required.add("end");
        required.add("step");
        return required;
    }

    @Override
    public void compress(String inputPath, String outputPath, HashMap<String, Integer> required) throws IOException {
        int start = required.get("start"); // get the start value from the user
        int end = required.get("end"); // get the end value from the user
        int step = required.get("step"); // get the step value from the user
        HashMap<Integer , Integer> quantizedValues = getQuantizedValues(start , end , step); // get the quantized values
        int[][] originalImage = ImageHandler2d.readImage(inputPath); // read the image
        int width = originalImage.length; // get the width of the image
        int height = originalImage[0].length; // get the height of the image
        int[][] quantizedDifference = predict(originalImage , quantizedValues); // get the quantized difference
        Files.write(Paths.get(outputPath) , (start + " " + end + " " + step + "\n").getBytes() , StandardOpenOption.APPEND); // write the start , end and step values
        Files.write(Paths.get(outputPath) , (width + " " + height + "\n").getBytes() , StandardOpenOption.APPEND); // write the width and height of the image
        for(int i = 0;i<width;i++){
            Files.write(Paths.get(outputPath) , (originalImage[i][0] + " ").getBytes() , StandardOpenOption.APPEND); // write the first row
        }
        Files.write(Paths.get(outputPath) , "\n".getBytes() , StandardOpenOption.APPEND); // write a new line
        for(int i = 0;i<height;i++){
            Files.write(Paths.get(outputPath) , (originalImage[0][i] + " ").getBytes() , StandardOpenOption.APPEND); // write the first column
        }
        Files.write(Paths.get(outputPath) , "\n".getBytes() , StandardOpenOption.APPEND); // write a new line
        for(int i = 0;i<width;i++){
            for(int j = 0;j<height;j++){
                Files.write(Paths.get(outputPath) , (quantizedDifference[i][j] + " ").getBytes() , StandardOpenOption.APPEND); // write the quantized difference
            }
            Files.write(Paths.get(outputPath) , "\n".getBytes() , StandardOpenOption.APPEND); // write a new line
        }
    }
    public int[][] predict(int[][] originalImage , HashMap<Integer , Integer>quantized){
        int width = originalImage.length; // get the width of the image
        int height = originalImage[0].length; // get the height of the image
        int [][] decodedImage = new int[width][height]; // create a new image to store the decoded image
        // copy first row and first column
        int[][] quantizedDifference = new int[width][height]; // create a new image to store the quantized difference
        for(int i = 0;i<width;i++){
            decodedImage[i][0] = originalImage[i][0]; // copy the first row
            quantizedDifference[i][0] = 0; // set the first row of the quantized difference to 0
        }
        for(int i = 0;i<height;i++){
            decodedImage[0][i] = originalImage[0][i]; // copy the first column
            quantizedDifference[0][i] = 0; // set the first column of the quantized difference to 0
        }
        // predict the rest of the image based on the decoded image
        for(int i = 1;i<width;i++){
            for(int j = 1;j<height;j++){
                int a = decodedImage[i][j-1];  // get the left pixel
                int b = decodedImage[i-1][j-1]; // get the top left pixel
                int c = decodedImage[i-1][j]; // get the top pixel
                int predictedValue; // the predicted value
                if(b <= Math.min(a,c)){ // if b is less than or equal to the minimum of a and c
                    predictedValue = Math.max(a,c); // the predicted value is the maximum of a and c
                }
                else if(b>= Math.max(a,c)){ // if b is greater than or equal to the maximum of a and c
                    predictedValue = Math.min(a,c); // the predicted value is the minimum of a and c
                }
                else{
                    predictedValue = a + c - b; // the predicted value is a + c - b
                }
                quantizedDifference[i][j] = quantized.get(originalImage[i][j] - predictedValue); // get the quantized difference
            }
        }
        return quantizedDifference;
    }
    public HashMap<Integer , Integer>getQuantizedValues(int start , int end , int step){
        HashMap<Integer , Integer> quantizedValues = new HashMap<>();
        int index = 0;
        while(start < end){
            int nowStart = start;
            int nowEnd = start + step;
            double mid = (double)(nowStart + nowEnd) / 2;
            for(int i = nowStart;i<=nowEnd;i++){
                quantizedValues.put(i , index);
            }
            index++;
            start = nowEnd + 1;
        }
        return quantizedValues;
    }
    public HashMap<Integer , Double> getDeQuantizedValues(int start , int end , int step){
        HashMap<Integer , Double> deQuantizedValues = new HashMap<>();
        int index = 0;
        while(start < end){
            int nowStart = start;
            int nowEnd = start + step;
            double mid = (double)(nowStart + nowEnd) / 2;
            deQuantizedValues.put(index , mid);
            index++;
            start = nowEnd + 1;
        }
        return deQuantizedValues;
    }

    @Override
    public void decompress(String inputPath, String outputPath, HashMap<String, Integer> required) throws IOException {
       // read from the file
       File file = new File(inputPath);
       // first line is the start
        Scanner sc = new Scanner(file);
        int start = sc.nextInt(); // get the start value
        int end = sc.nextInt(); // get the end value
        int step = sc.nextInt(); // get the step value
        int width = sc.nextInt(); // get the width of the image
        int height = sc.nextInt(); // get the height of the image
        int[][] decodedImage = new int[width][height]; // create a new image to store the decoded image
        int[][] quantizedDifference = new int[width][height]; // create a new image to store the quantized difference
        HashMap<Integer , Double> DeQuantizedValues = new HashMap<>(); // create a new hashmap to store the dequantized values
        // read first row and first column
        for(int i = 0;i<width;i++){
            decodedImage[i][0] = sc.nextInt(); // read the first row
        }
        for(int i = 0;i<height;i++){
            decodedImage[0][i] = sc.nextInt(); // read the first column
        }
        // read the quantized difference
        for(int i = 0;i<width;i++){
            for(int j = 0;j<height;j++){
                quantizedDifference[i][j] = sc.nextInt(); // read the quantized difference
            }
        }
       HashMap<Integer, Double> deQuantizedValues = getDeQuantizedValues(start, end, step); // get the dequantized values
         // decode the image
        for(int i = 1;i<width;i++){
            for(int j = 1;j<height;j++){
                int a = decodedImage[i][j-1];  // get the left pixel
                int b = decodedImage[i-1][j-1]; // get the top left pixel
                int c = decodedImage[i-1][j]; // get the top pixel
                int predictedValue; // the predicted value
                if(b <= Math.min(a,c)){ // if b is less than or equal to the minimum of a and c
                    predictedValue = Math.max(a,c); // the predicted value is the maximum of a and c
                }
                else if(b>= Math.max(a,c)){ // if b is greater than or equal to the maximum of a and c
                    predictedValue = Math.min(a,c); // the predicted value is the minimum of a and c
                }
                else{
                    predictedValue = a + c - b; // the predicted value is a + c - b
                }
                decodedImage[i][j] = (int)(deQuantizedValues.get(quantizedDifference[i][j]) + predictedValue ); // get the decoded image
            }
        }
        ImageHandler2d.writeImage(decodedImage , outputPath);
    }
}
