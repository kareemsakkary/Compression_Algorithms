import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class LZW implements Algorithm{
    private HashMap<String, Integer> dictionary;
    ArrayList<Integer> codes ;
    String file = "";
    String input = "";

    public ArrayList<Integer> compress(){
        ArrayList<Integer> codes = new ArrayList<>();
        HashMap<String, Integer> table = new HashMap<>();
        for(int i = 0 ; i < 256 ; i++){
            table.put( "" + (char) i , i);
        }
        int code = 256;
        String s1 = input.charAt(0)+"";
        String s2 ;
        for(int i = 1 ; i < input.length() ; i++){
            s2 = "" + input.charAt(i);
            while(i < input.length() && table.containsKey(s1+s2)){
                s1 = s1 + s2;
                i++;
                if(i < input.length())
                    s2 = "" + input.charAt(i);
            }
            codes.add(table.get(s1));
            table.put(s1+s2,code++);
            s1 = s2;
        }
        codes.add(table.get(s1));
        return codes;
    }

    public String decompress(){
        StringBuilder output = new StringBuilder();
        HashMap<Integer, String> table = new HashMap<>();
        for(int i = 0 ; i < 256 ; i++){
            table.put(i , "" + (char) i);
        }
        int code = 256;
        String s1 = table.get(codes.get(0));
        String s2;
        output.append(s1);
        for(int i = 1 ; i < codes.size() ; i++){
            int currCode =  codes.get(i);
            if(!table.containsKey(currCode)){
                s2 = s1 + s1.charAt(0);
            }else {
                s2 = table.get(currCode);
            }
            output.append(s2);
            table.put(code++,s1 + s2.charAt(0));
            s1 = s2;
        }
        return output.toString();
    }

    public boolean readFile1(){
        Path path = Path.of(file);
        try {
            input = Files.readString(path);
            return true;
        }
        catch (IOException ex) {
            return false;
        }
    }
    public boolean readFile2(){
        try {
            codes = inputTwoBytesFile(file);
            return true;
        }
        catch (IOException ex) {
            return false;
        }
    }
    public void writeToFile(ArrayList<Integer> data) {
        if (file != "") {
            String newName;
            newName = "Output_compressed.txt";
            outputTwoBytesFile(data,newName);
        }
    }
    public void writeToFile(String str) {
        if (file != "") {
            String newName;
            newName = "Output_decompressed.txt";
            Path path = Paths.get(newName);
            try {
                Files.writeString(path, str, StandardCharsets.UTF_8);
            }
            catch (IOException ex) {

            }
        }
    }
    public static void outputTwoBytesFile(ArrayList<Integer> codes , String file_name){
        try (DataOutputStream fos = new DataOutputStream(new FileOutputStream(file_name))) {
            // Write the byte array to the file
            for (int code : codes) {
                fos.writeShort((short)code);
            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static ArrayList<Integer> inputTwoBytesFile(String file_name) throws FileNotFoundException {
        ArrayList<Integer> codes = new ArrayList<>();

        try (DataInputStream fos = new DataInputStream(new FileInputStream(file_name))) {
            // Write the byte array to the file
            short re;
            while (fos.available() > 0){
                re = fos.readShort();
                codes.add((int)re);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return codes;
    }
    @Override
    public String getAlgorithmName() {
        return "LZW-Algorithm";
    }

    @Override
    public void compress(String inputPath, String outputPath) {
        file = inputPath;
        if(readFile1()){
            ArrayList<Integer> data = compress();
            writeToFile(data);
            JOptionPane.showMessageDialog(null, "Done Successfully!", "File Compressed",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        else{
            // show message box error
            JOptionPane.showMessageDialog(null, "Error, file not found.", "Invalid File",
                    JOptionPane.ERROR_MESSAGE);

        }
    }

    @Override
    public void decompress(String inputPath, String outputPath) {
        file = inputPath;
        if(readFile2()){
            String data = decompress();
            writeToFile(data);
            JOptionPane.showMessageDialog(null, "Done Successfully!", "File Decompressed",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        else{
            // show message box error
            JOptionPane.showMessageDialog(null, "Error, file not found.", "Invalid File",
                    JOptionPane.ERROR_MESSAGE);

        }
    }
}
