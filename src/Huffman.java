import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

class Node{
    public char c;
    public int freq;
    public Node left;
    public Node right;
    public Node(char c , int freq){
        this.c = c;
        this.freq = freq;
    }
    public Node(char c , int freq , Node left , Node right){
        this.c = c;
        this.freq = freq;
        this.left = left;
        this.right = right;
    }
    public String toString(){
        return c + " " + freq;
    }
}
public class Huffman implements Algorithm{
    @Override
    public String getAlgorithmName() {
        return "Huffman";
    }
    public static HashMap<Character,String> buildTable(HashMap<Character,Integer> freq){
        PriorityQueue<Node> pq = new PriorityQueue<>(freq.size() , new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o1.freq - o2.freq;
            }
        });
        for(Map.Entry<Character , Integer> entry : freq.entrySet()){
            pq.add(new Node(entry.getKey() , entry.getValue()));
        }
        while(pq.size() != 1){
            Node left = pq.poll(); // poll() : Retrieves and removes the head of this queue, or returns null if this queue is empty.
            Node right = pq.poll();
            Node parent = new Node('\0' , left.freq + right.freq);
            parent.left = left;
            parent.right = right;
            pq.add(parent);
        }
        Node root = pq.peek(); // peek() : Retrieves, but does not remove, the head of this queue, or returns null if this queue is empty.
        HashMap<Character , String> codes = new HashMap<>(); // to store the codes of each character
        getCodes(root , "" , codes);
        return codes;
    }
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
    private static void getCodes(Node root, String s, HashMap<Character, String> codes) { // a method to get the codes of each character
        if(root == null) return; // if the node is null, return
        if(root.left == null && root.right == null){ // if the node is a leaf
            codes.put(root.c , s); // add the code of the character to the hashmap
            return;
        }
        getCodes(root.left , s+"0" , codes); // go to the left child
        getCodes(root.right , s+"1" , codes); // go to the right child
    }
    public static String huffmanEncode(String s){
        HashMap<Character , Integer> freq = new HashMap<>(); // to store the frequency of each character
        for(int i = 0;i<s.length();i++){ // iterate over the string
            if(freq.containsKey(s.charAt(i))){ // if the character is already in the hashmap
                freq.put(s.charAt(i) , freq.get(s.charAt(i))+1); // increment its frequency
            }
            else{
                freq.put(s.charAt(i) , 1); // add the character to the hashmap with a frequency of 1
            }
        }
        HashMap<Character , String> codes = buildTable(freq); // to get the codes of each character
        // to get the codes of each character
        StringBuilder encoded = new StringBuilder(); // to store the encoded string
        for(int i = 0;i<s.length();i++){ // iterate over the string
            encoded.append(codes.get(s.charAt(i))); // add the code of the character to the encoded string
        }
        String ret = byteToString((byte) freq.size()) ; // the overhead of the encoded string
        for(Map.Entry<Character , Integer> entry : freq.entrySet()){ // iterate over the hashmap
            ret += byteToString((byte)((int)entry.getKey())) + shortToString(entry.getValue().shortValue());
            // add the character and its frequency to the overhead
        }
        ret += encoded.toString();
        System.out.println(ret);
        return ret;
    }
    public static String huffmanDecode(String code){
        int ign = (int)stringToByte(code.substring(0 , 8)); // first 8 bytes are the number of bits to be ignored
        int sz = (int)stringToByte(code.substring(8 , 16)); // next 8 bytes are the size of the hashmap
        HashMap<Character , Integer> freq = new HashMap<>(); // to store the frequency of each character
        int i = 16; // the index of the current byte
        while(sz-- > 0){ // iterate over the hashmap
            char c = (char)stringToByte(code.substring(i , i+8)); // get the character
            i += 8; // increment the index
            int f = (int)stringToShort(code.substring(i , i+16)); // get the frequency
            i += 16; // increment the index
            freq.put(c , f); // add the character and its frequency to the hashmap
        }
        HashMap<Character , String> codes = buildTable(freq); // to get the codes of each character
        HashMap<String, Character> revCodes = new HashMap<>(); // to get the character of each code
        for(Map.Entry<Character , String> entry : codes.entrySet()){ // iterate over the hashmap
            revCodes.put(entry.getValue() , entry.getKey()); // add the code and its character to the hashmap
        }
        String ret = ""; // to store the decoded string
        String cur = ""; // to store the current code
        while(i < code.length() - ign){ // iterate over the encoded string while ignoring the last few bits
            cur += code.charAt(i); // add the current bit to the current code
            if(revCodes.containsKey(cur)){ // if the current code is a valid code
                ret += revCodes.get(cur);
                cur = "";
            }
            i++;
        }
        System.out.println(ret);
        return ret;
    }

    @Override
    public void compress(String inputPath, String outputPath) throws IOException, IOException {
        File file = new File(inputPath);
        String content = "";
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            content += sc.nextLine();
            if(sc.hasNextLine()) content += "\n";
        }
        String encoded = huffmanEncode(content);
        char ign =(char)(8 - (encoded.length() % 8)); // the number of bits to be ignored , as the encoded string may not be a multiple of 8
        String curr = ""; // to store the encoded string
        curr += ign; // add the number of bits to be ignored to the beginning of the string, so that it can be retrieved later
        for(int i = 0 ; i < encoded.length() ; i+=8){ // iterate over the encoded string
            curr += (char) stringToByte(encoded.substring(i , Math.min(i+8 , encoded.length()))); // add the byte to the string
        }
        Files.write(Paths.get(outputPath) , curr.getBytes());
    }

    @Override
    public void decompress(String inputPath, String outputPath) throws IOException {
        File file = new File(inputPath);
        String content = "";
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            content += sc.nextLine();
            if(sc.hasNextLine()) content += "\n";
        }
        String data = "";
        for(int i = 0 ; i < content.length() ; i++){
            data += byteToString((byte)content.charAt(i));
        }

        String decoded = huffmanDecode(data);
        Files.write(Paths.get(outputPath) , decoded.getBytes());
    }
}
