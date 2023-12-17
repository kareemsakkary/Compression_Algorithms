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
        required.add("step");
        return required;
    }

    @Override
    public void compress(String inputPath, String outputPath, HashMap<String, Integer> required) throws IOException {
        int start = -256;
        int end = 255;
        int step = required.get("step"); // get the step value from the user
        int[][] originalImage = ImageHandler2d.readImage(inputPath); // read the image
        int height = originalImage.length;
        int width = originalImage[0].length;
        HashMap<Integer , Double> deQuantizedValues = getDeQuantizedValues(start , end , step); // get the dequantized values
        int[][] quantizedDifference = predict(originalImage , deQuantizedValues, start , end , step); // get the quantized difference
        String content = "";
        content+= start + " " + end + " " + step + "\n"; // write the start , end and step
        content+= height + " " + width + "\n"; // write the height and width
        for(int i = 0;i<height;i++){
            content += originalImage[i][0] + " "; // write the first column
        }
        content += "\n"; // write a new line
        for(int i = 0;i<width;i++){
            content += originalImage[0][i] + " "; // write the first row
        }
        // store the quantized difference
        content += "\n"; // write a new line
        for(int i = 0;i<height;i++){
            for(int j = 0;j<width;j++){
                content += quantizedDifference[i][j] + " "; // write the quantized difference
            }
            content += "\n"; // write a new line
        }
        Files.write(Paths.get(outputPath) , content.getBytes());
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
    public int[][] predict(int[][] originalImage , HashMap<Integer , Double> deQuantized, int start , int end ,int step){
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
                int quantized = getQuantizedValue(start , end , step , temp - predictedValue); // get the quantized value
                quantizedDifference[i][j] = quantized; // store the quantized value
                decodedImage[i][j] = (int)(predictedValue + deQuantized.get(quantizedDifference[i][j])); // get the decoded image
            }
        }
        return quantizedDifference;
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
    public int getQuantizedValue(int start , int end , int step , int val){
        return (int)Math.ceil((double)(val - start) / step);
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
