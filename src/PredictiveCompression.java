import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class PredictiveCompression implements Algorithm{

    public static String byteToString(byte num){ // a method to convert a byte to a string of 8 bits
        String ret = ""; // the string to be returned
        for(int i = 7 ; i >= 0 ; i--){ // iterate over the 8 bits of the byte
            if((num & (1 << i)) != 0){ // if the bit is 1
                ret += '1'; // add 1 to the string
            }
            else{
                ret += '0'; // add 0 to the string
            }
        }
        return ret; // return the string
    }
    public static byte stringToByte(String s){ // a method to convert a string of 8 bits to a byte
        byte ret = 0; // the byte to be returned
        for(int i = 0 ; i < s.length() ; i++){ // iterate over the 8 bits of the string
            if(s.charAt(i) == '1'){ // if the bit is 1
                ret |= (1 << (7-i)); // set the bit at the same position in the byte to 1
            }
        }
        return ret; // return the byte
    }
    public static String shortToString(short num){ // a method to convert a short to a string of 16 bits (2 bytes)
        String ret = "";
        for(int i = 15 ; i >= 0 ; i--){
            if((num & (1 << i)) != 0){
                ret += '1';
            }
            else{
                ret += '0';
            }
        }
        return ret;
    }
    public static short stringToShort(String s){ // a method to convert a string of 16 bits (2 bytes) to a short
        short ret = 0;
        for(int i = 0 ; i < s.length() ; i++){
            if(s.charAt(i) == '1'){
                ret |= (short) (1 << (15-i));
            }
        }
        return ret;
    }

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
        short start = -256;
        short end = 255;
        short step = required.get("step").shortValue(); // get the step value from the user
        int[][] originalImage = ImageHandler2d.readImage(inputPath); // read the image
        short height = (short) originalImage.length;
        short width = (short) originalImage[0].length;
        HashMap<Integer , Double> deQuantizedValues = getDeQuantizedValues(start , end , step); // get the dequantized values
        int[][] quantizedDifference = predict(originalImage , deQuantizedValues, start , end , step); // get the quantized difference
        String content = "";

        content+= shortToString(start) +  shortToString(end) +  shortToString(step) ; // write the start , end and step
        content+= shortToString(height) +  shortToString(width); // write the height and width
        for(int i = 0;i<height;i++){
            content += shortToString((short) originalImage[i][0]) ; // write the first column
        }
        for(int i = 0;i<width;i++){
            content +=  shortToString((short)originalImage[0][i]); // write the first row
        }
        // store the quantized difference
        for(int i = 0;i<height;i++){
            for(int j = 0;j<width;j++){
                content += shortToString((short)quantizedDifference[i][j]); // write the quantized difference
            }
        }
        String data = "";
        for(int i = 0 ; i < content.length() ; i+=8){
            String now = content.substring(i , Math.min(i+8 , content.length()));
            data += (char)stringToByte(now);
        }
        Files.write(Paths.get(outputPath) , data.getBytes());
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
                decodedImage[i][j] = Math.max(decodedImage[i][j] , 0); // make sure the value is not less than 0
                decodedImage[i][j] = Math.min(decodedImage[i][j] , 255); // make sure the value is not greater than 255
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
       // first line is the start
        // read first line
        String data = Files.readString(Path.of(inputPath));
        String content = "";
        for(int i = 0 ; i < data.length() ; i++){
            content += byteToString((byte)data.charAt(i));
        }
        int index = 0;
        int start = stringToShort(content.substring(index,index+16));// get the start
        index+=16;
        int end = stringToShort(content.substring(index,index+16)); // get the end
        index+=16;
        int step = stringToShort(content.substring(index,index+16)); // get the step
        index+=16;
        int height = stringToShort(content.substring(index,index+16)); // get the width
        index+=16;
        int width = stringToShort(content.substring(index,index+16)); // get the height
        index+=16;

        int[][] decodedImage = new int[height][width]; // create a new image to store the decoded image
        for(int i = 0;i<height;i++){
            decodedImage[i][0] = stringToShort(content.substring(index,index+16)); // take the first column
            index+=16;
        }
        for(int i = 0;i<width;i++){
            decodedImage[0][i] =  stringToShort(content.substring(index,index+16)); // take the first row
            index+=16;
        }
        int[][]quantizedDifference = new int[height][width]; // create a new image to store the quantized difference
        for(int i = 0;i<height;i++){
            for(int j = 0;j<width;j++){
                quantizedDifference[i][j] =  stringToShort(content.substring(index,index+16)); // take the quantized difference
                index+=16;
            }
        }
        HashMap<Integer , Double> deQuantized = getDeQuantizedValues(start , end , step); // get the dequantized values
        for(int i =1;i<height;i++){
            for(int j =1;j<width;j++){
               int predictedValue = getPredictedValue(decodedImage[i-1][j] , decodedImage[i][j-1] , decodedImage[i-1][j-1]); // get the predicted value
               decodedImage[i][j] = (int)(predictedValue + deQuantized.get(quantizedDifference[i][j])); // get the decoded image
                decodedImage[i][j] = Math.max(decodedImage[i][j] , 0); // make sure the value is not less than 0
                decodedImage[i][j] = Math.min(decodedImage[i][j] , 255); // make sure the value is not greater than 255
            }
        }
        ImageHandler2d.writeImage(decodedImage , outputPath);
    }
}
