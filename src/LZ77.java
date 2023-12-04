import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LZ77 implements Algorithm{


    public static class Tag {
        int position;
        int length;
        char nextSymbol;

        Tag(int position, int length, char nextSymbol) {
            this.position = position;
            this.length = length;
            this.nextSymbol = nextSymbol;
        }
    }
    public List<Tag> compress(String input){
        List<Tag> tags = new ArrayList<>();
        for(int i = 0 ; i < input.length() ;){
            int longestMatch = 0;
            int postion = 0;
            char nextSymbol = input.charAt(i);
            for(int j = 0; j < i; j++){
                int match = 0;
                while (i+match < input.length() && j+match < i && input.charAt(i+match) == input.charAt(j+match)){match++;}
                if(match > longestMatch){
                    longestMatch = match;
                    postion = i - j;
                    if(i+longestMatch < input.length()) nextSymbol =   input.charAt(i+longestMatch);
                    else nextSymbol = '_';
                }
            }
            tags.add(new Tag(postion,longestMatch,nextSymbol));
            i += longestMatch+1;
        }
        return tags;
    }

    public String decompress(List<Tag> tags){
        StringBuilder output = new StringBuilder();
        for(Tag t : tags){
            int pos = t.position;
            int len = t.length;
            char nextSymb = t.nextSymbol;

            if(pos == 0 && len == 0){
                output.append(nextSymb);
            }
            else{
                int startI = output.length() - pos;
                int endI = startI + len;
                if(endI > output.length())
                    endI = output.length();
                for(int i = startI; i < endI; i++){
                    output.append(output.charAt(i));
                }
                output.append(nextSymb);
            }
        }
        output.deleteCharAt(output.length()-1);
        return output.toString();
    }

    @Override
    public String getAlgorithmName() {
        return "LZ77-Algorithm";
    }

    @Override
    public ArrayList<String> getRequiredData() {
        return null;
    }

    @Override
    public void compress(String inputPath, String outputPath, HashMap<String,Integer> required) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(inputPath));
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
        StringBuilder input = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            input.append(line);
        }
        List<Tag> tags = compress(input.toString());
        for(Tag tag : tags){
            writer.write(tag.position + " " + tag.length + " " + tag.nextSymbol);
            writer.newLine();
        }
        reader.close();
        writer.close();
    }

    @Override
    public void decompress(String inputPath, String outputPath, HashMap<String,Integer> required) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("input_compressed.txt"));
        BufferedWriter writer = new BufferedWriter(new FileWriter("output_decompressed.txt"));
        List<Tag> tags = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            String temp[] = line.split(" ");
            tags.add(new Tag(Integer.parseInt(temp[0]),Integer.parseInt(temp[1]),temp[2].charAt(0)));
        }
        String data = decompress(tags);
        writer.write(data);
        reader.close();
        writer.close();
    }
}
