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
        int height = originalImage.length;
        int width = originalImage[0].length;
        HashMap<Integer , Double> deQuantizedValues = getDeQuantizedValues(start , end , step); // get the dequantized values
        int[][] quantizedDifference = predict(originalImage , quantizedValues , deQuantizedValues); // get the quantized difference
        // clear the file
        Files.write(Paths.get(outputPath) , "".getBytes());
        Files.write(Paths.get(outputPath) , (start + " " + end + " " + step + "\n").getBytes() , StandardOpenOption.APPEND); // write the start , end and step values
        Files.write(Paths.get(outputPath) , (height + " " + width + "\n").getBytes() , StandardOpenOption.APPEND); // write the width and height of the image
        for(int i = 0;i<height;i++){
            Files.write(Paths.get(outputPath) , (originalImage[i][0] + " ").getBytes() , StandardOpenOption.APPEND); // write the first column
        }
        Files.write(Paths.get(outputPath) , "\n".getBytes() , StandardOpenOption.APPEND); // write a new line
        for(int i = 0;i<width;i++){
            Files.write(Paths.get(outputPath) , (originalImage[0][i] + " ").getBytes() , StandardOpenOption.APPEND); // write the first r
        }
        // store the quantized difference
        Files.write(Paths.get(outputPath) , "\n".getBytes() , StandardOpenOption.APPEND); // write a new line
        for(int i = 0;i<height;i++){
            for(int j = 0;j<width;j++){
                Files.write(Paths.get(outputPath) , (quantizedDifference[i][j] + " ").getBytes() , StandardOpenOption.APPEND); // write the quantized difference
            }
            Files.write(Paths.get(outputPath) , "\n".getBytes() , StandardOpenOption.APPEND); // write a new line
        }
    }
    public int getPredictedValue(int a , int b  , int c){
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
        return predictedValue; // return the predicted value
    }
    public int[][] predict(int[][] originalImage , HashMap<Integer , Integer>quantized , HashMap<Integer , Double> deQuantized){
        int height = originalImage.length; // get the width of the image
        int width = originalImage[0].length; // get the height of the image
        int[][] decodedImage = new int[height][width]; // create a new image to store the decoded image
        // copy first row and first column
        int[][] quantizedDifference = new int[height][width]; // create a new image to store the quantized difference
        for(int i = 0;i<width;i++){
            decodedImage[0][i] = originalImage[0][i]; // copy the first row
            quantizedDifference[0][i] = 0; // set the first row of the quantized difference to 0
        }
        for(int i = 0;i<height;i++){
            decodedImage[i][0] = originalImage[i][0]; // copy the first column
            quantizedDifference[i][0] = 0; // set the first column of the quantized difference to 0
        }
        // predict the rest of the image based on the decoded image
        for(int i = 1;i<height;i++){
            for(int j = 1;j<width;j++){
                int predictedValue = getPredictedValue(decodedImage[i-1][j] , decodedImage[i][j-1] , decodedImage[i-1][j-1]); // get the predicted value
                int temp = originalImage[i][j]; // get the current value
                quantizedDifference[i][j] = quantized.get(temp - predictedValue); // get the quantized difference
                decodedImage[i][j] = (int)(predictedValue + deQuantized.get(quantizedDifference[i][j])); // get the decoded image
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
            for(int i = nowStart;i<nowEnd;i++){
                quantizedValues.put(i , index);
            }
            index++;
            start = nowEnd;
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
            start = nowEnd;
        }
        return deQuantizedValues;
    }

    @Override
    public void decompress(String inputPath, String outputPath, HashMap<String, Integer> required) throws IOException {
       // read from the file
       File file = new File(inputPath);
       // first line is the start
        // read first line
        Scanner sc = new Scanner(file);
       String line1 = sc.nextLine();
       String[] line1Split = line1.split(" ");
       int start = Integer.parseInt(line1Split[0]); // get the start
        int end = Integer.parseInt(line1Split[1]); // get the end
        int step = Integer.parseInt(line1Split[2]); // get the step
        String line2 = sc.nextLine();
        String[] line2Split = line2.split(" ");
        int height = Integer.parseInt(line2Split[0]); // get the width
        int width = Integer.parseInt(line2Split[1]); // get the height
        String firstColumn = sc.nextLine();
        String[] firstColumnSplit = firstColumn.split(" ");
        String firstRow = sc.nextLine();
        String[] firstRowSplit = firstRow.split(" ");
        int[][] decodedImage = new int[height][width]; // create a new image to store the decoded image
        for(int i = 0;i<height;i++){
            decodedImage[i][0] = Integer.parseInt(firstColumnSplit[i]); // take the first column
        }
        for(int i = 0;i<width;i++){
            decodedImage[0][i] = Integer.parseInt(firstRowSplit[i]); // take the first row
        }
        int[][]quantizedDifference = new int[height][width]; // create a new image to store the quantized difference
        for(int i = 0;i<height;i++){
            String line = sc.nextLine();
            String[] lineSplit = line.split(" ");
            for(int j = 0;j<width;j++){
                quantizedDifference[i][j] = Integer.parseInt(lineSplit[j]); // take the quantized difference
            }
        }
        HashMap<Integer , Double> deQuantized = getDeQuantizedValues(start , end , step); // get the dequantized values
        for(int i =1;i<height;i++){
            for(int j =1;j<width;j++){
               int predictedValue = getPredictedValue(decodedImage[i-1][j] , decodedImage[i][j-1] , decodedImage[i-1][j-1]); // get the predicted value
               decodedImage[i][j] = (int)(predictedValue + deQuantized.get(quantizedDifference[i][j])); // get the decoded image
            }
        }
        ImageHandler2d.writeImage(decodedImage , outputPath);
    }
}
