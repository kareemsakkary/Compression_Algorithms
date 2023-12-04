import java.io.*;
import java.util.ArrayList;
import java.util.Vector;

public class VectorQuantization implements Algorithm {
    ArrayList<ArrayList<Integer>> pixels;
    int picWidth , picHeight;
    int vectorWidth , vectorHeight;
    int codebookSize;
    ImageHandler imageHandler ;
    public VectorQuantization() {
        imageHandler = new ImageHandler();
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
    public ArrayList<ArrayList<ArrayList<Integer>>> getVectors(){
        ArrayList<ArrayList<ArrayList<Integer>>>  vectors = new ArrayList<>();
        for(int i = 0 ; i < picHeight ; i += vectorHeight){
            for(int j = 0 ; j < picWidth ; j += vectorWidth){
                ArrayList<ArrayList<Integer>> vector = new ArrayList<>();
                for(int k = i ; k < i + vectorHeight ; k++){
                    ArrayList<Integer> temp = new ArrayList<>();
                    for(int l = j ; l < j + vectorWidth ; l++){
                        temp.add(pixels.get(k).get(l));
                    }
                    vector.add(temp);
                }
                vectors.add(vector);
            }
        }
        return vectors;
    }
    public ArrayList<ArrayList<Integer>> calculateAvg(ArrayList<ArrayList<ArrayList<Integer>>>vectors){
        ArrayList<ArrayList<Integer>> avg = new ArrayList<>();
        for(int i = 0 ; i < vectors.get(0).size() ; i++){
            ArrayList<Integer> temp = new ArrayList<>();
            for(int j = 0 ; j < vectors.get(0).get(0).size() ; j++){
                int sum = 0;
                for(int k = 0 ; k < vectors.size() ; k++){
                    sum += vectors.get(k).get(i).get(j);
                }
                temp.add(sum / (vectors.size()));
            }
            avg.add(temp);
        }
        return avg;
    }
    public double calculateError(ArrayList<ArrayList<Integer>> vector1 , ArrayList<ArrayList<Integer>> vector2){
        double error = 0;
        for(int i = 0 ; i < vector1.size() ; i++){
            for(int j = 0 ; j < vector1.get(0).size() ; j++){
                error += Math.pow(vector1.get(i).get(j) - vector2.get(i).get(j) , 2);
            }
        }
        return error;
    }
    public ArrayList<ArrayList<ArrayList<Integer>>> optimize(ArrayList<ArrayList<ArrayList<Integer>>> codebook, ArrayList<ArrayList<ArrayList<Integer>>>vectors ,int[] groups){
            boolean flag = true;
            ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> newGroups = new ArrayList<>();
            while(flag){
                flag = false;
                for(int i = 0 ; i<vectors.size();i++){
                    double  minError = Double.MAX_VALUE;
                    int min_index = 0;
                    for(int j = 0 ; j<codebook.size();j++){
                        double error = calculateError(vectors.get(i),codebook.get(j));
                        if(error < minError){
                            minError = error;
                            min_index = j;
                            vectors.get(i).clear();
                            vectors.get(i).addAll(codebook.get(j));
                        }
                    }
                    if(groups[i] != min_index){
                        flag = true;
                        groups[i] = min_index; // update the group of the vector
                        newGroups.get(min_index).add(vectors.get(i)); // add the vector to the new codebook
                    }
                }
                // calculate the average of each group
                for(int i = 0 ; i<newGroups.size();i++){
                    ArrayList<ArrayList<Integer>> avg = calculateAvg(newGroups.get(i));
                    codebook.set(i , avg);
                }
            }
            return codebook;
    }
    public ArrayList<ArrayList<ArrayList<Integer>>> getCodesBookHelper(ArrayList<ArrayList<ArrayList<Integer>>> vectors,ArrayList<ArrayList<ArrayList<Integer>>> codebook){
        if(codebook.size() == codebookSize){
            int[] groups = new int[vectors.size()];
            for(int i = 0 ; i<vectors.size();i++){
                double  minError = Double.MAX_VALUE;
                int min_index = 0;
                for(int j = 0 ; j<codebook.size();j++){
                    double error = calculateError(vectors.get(i),codebook.get(j));
                    if(error < minError){
                        minError = error;
                        min_index = j;
                        vectors.get(i).clear();
                        vectors.get(i).addAll(codebook.get(j));
                    }
                }
                groups[i] = min_index;
            }
            codebook = optimize(codebook , vectors , groups);
            return codebook;
        }
        ArrayList<ArrayList<ArrayList<Integer>>> newCodebook = new ArrayList<>();
        for(int i = 0 ; i < codebook.size() ; i++){
            ArrayList<ArrayList<Integer>> code1,code2; // code1 is the codebook vector and code2 is the codebook vector + 1
            code1 = new ArrayList<>(); // initialize the code1 and code2
            code2 = new ArrayList<>(); // initialize the code1 and code2
            for(int x = 0 ; x < vectorHeight ; x++){ // loop on the vector height
                ArrayList<Integer> temp1, temp2; // temp1 is the codebook vector and temp2 is the codebook vector + 1
                temp1 = new ArrayList<>(); // initialize the temp1 and temp2
                temp2 = new ArrayList<>(); // initialize the temp1 and temp2
                for(int y = 0 ; y < vectorWidth ; y++){ // loop on the vector width
                    temp1.add((int)Math.floor(codebook.get(i).get(x).get(y))); // add the floor of the codebook vector to the temp1
                    temp2.add((int)Math.floor(codebook.get(i).get(x).get(y))+1); // add the floor of the codebook vector + 1 to the temp2
                } // end of the loop on the vector width
                code1.add(temp1); // add the temp1 to the code1
                code2.add(temp2); // add the temp2 to the code2
            }
            newCodebook.add(code1); // add the code1 to the new codebook
            newCodebook.add(code2);  // add the code2 to the new codebook
        }
        // now splited the codebook into two codebooks
        ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> newGroups = new ArrayList<>(); // initialize the groups array, new Groups is an array of groups
        for(int i = 0 ; i < newCodebook.size() ; i++){ // initialize the groups array
            ArrayList<ArrayList<ArrayList<Integer>>> group = new ArrayList<>(); // initialize the group
            newGroups.add(group); // add the group to the groups array
        }
        int[] groupIndex = new int[vectors.size()]; // an array to store the index of the group of each vector
            for(int i = 0 ; i < newCodebook.size() ; i++){ // initialize the groups array
                ArrayList<ArrayList<ArrayList<Integer>>> group = new ArrayList<>(); // initialize the group
                newGroups.set(i, group); // add the group to the groups array
            }
            for(int i = 0 ; i < vectors.size() ; i++){ // loop over all the vectors and assign each one to a group
                double minError = Integer.MAX_VALUE; // initialize the minimum error
                int minIndex = 0; // initialize the index of the group with the minimum error
                for(int j = 0 ; j < newCodebook.size() ; j++){ // loop over all the groups
                    double error = calculateError(vectors.get(i) , newCodebook.get(j)); // calculate the error between the vector and the group
                    if(error < minError){ // if the error is less than the minimum error
                        minError = error; // update the minimum error
                        minIndex = j; // update the index of the group with the minimum error

                    }
                }
                groupIndex[i] = minIndex;
                newGroups.get(minIndex).add(vectors.get(i));
            }
            for(int i = 0; i < newCodebook.size() ; i++){
                if(!newGroups.get(i).isEmpty())
                    newCodebook.set(i,calculateAvg(newGroups.get(i)));
            }

        return getCodesBookHelper(vectors,newCodebook);
    }
    public int getGreatestDivisor(int num , int from){
        for(int i = from ; i <= num ; i++){
            if(num % i == 0){
                return i;
            }
        }
        return num;
    }
    public ArrayList<ArrayList<ArrayList<Integer>>> getCodesBook(ArrayList<ArrayList<Integer>> pixels,int vectorWidth , int vectorHeight, int codebookSize){
        this.pixels = pixels;
        this.codebookSize = codebookSize;
        picHeight = pixels.size();
        picWidth = pixels.get(0).size();

        this.vectorWidth = getGreatestDivisor(picWidth , vectorWidth);
        this.vectorHeight = getGreatestDivisor(picHeight , vectorHeight);

        ArrayList<ArrayList<ArrayList<Integer>>> vectors = getVectors();
        ArrayList<ArrayList<Integer>> avg = calculateAvg(vectors);
        ArrayList<ArrayList<ArrayList<Integer>>> codebook = new ArrayList<>();
        codebook.add(avg);
        ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> groups = new ArrayList<>();
        groups.add(vectors);
        return getCodesBookHelper(vectors,codebook);
    }

    @Override
    public String getAlgorithmName() {
        return "Vector Quantization";
    }

    @Override
    public void compress(String inputPath, String outputPath) throws IOException {
        int [][] pixels = imageHandler.readImage(inputPath);
        ArrayList<ArrayList<Integer>> pixelsList = new ArrayList<>();
        for(int i = 0 ; i < pixels.length ; i++){
            ArrayList<Integer> temp = new ArrayList<>();
            for(int j = 0 ; j < pixels[0].length ; j++){
                temp.add(pixels[i][j]);
            }
            pixelsList.add(temp);
        }
        ArrayList<ArrayList<ArrayList<Integer>>> codebook = getCodesBook(pixelsList , 2 , 2 , 256);
        ArrayList<Integer> compressed = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<Integer>>> vectors = getVectors(); // get vectors from image
        for(int i = 0 ; i < vectors.size() ; i++){
            double minError = Integer.MAX_VALUE;
            int minIndex = 0;
            for(int j = 0 ; j < codebook.size() ; j++){
                double error = calculateError(vectors.get(i) , codebook.get(j)); // calculate error between vector and codebook
                if(error < minError){ // get the minimum error
                    minError = error; // update minimum error
                    minIndex = j; // update minimum index
                }
            }
            compressed.add(minIndex); // add the codebook vector to compressed list
        }
        // write the codebook to file
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputPath));
        bufferedWriter.write(codebook.size() + "\n");
        bufferedWriter.write(vectorHeight + "\n");
        bufferedWriter.write(vectorWidth + "\n");
        bufferedWriter.write(compressed.size() + "\n");
        bufferedWriter.write(pixels.length + "\n");
        bufferedWriter.write(pixels[0].length + "\n");
        for(int i = 0 ; i < codebook.size() ; i++){
            for(int j = 0 ; j < codebook.get(0).size() ; j++){
                for(int k = 0 ; k < codebook.get(0).get(0).size() ; k++){
                    bufferedWriter.write(codebook.get(i).get(j).get(k) + " ");
                }
                bufferedWriter.write("\n");
            }
        }
        for(int i = 0 ; i < compressed.size() ; i++){
            bufferedWriter.write(compressed.get(i) + " ");
        }
        bufferedWriter.close();
    }

    @Override
    public void decompress(String inputPath, String outputPath) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(inputPath));
        // first line
        int codebookSize = Integer.parseInt(bufferedReader.readLine());
        // second line
        vectorHeight = Integer.parseInt(bufferedReader.readLine());
        // third line
        vectorWidth = Integer.parseInt(bufferedReader.readLine());
        // fourth line
        int compressedSize = Integer.parseInt(bufferedReader.readLine());
        int height = Integer.parseInt(bufferedReader.readLine());
        int width = Integer.parseInt(bufferedReader.readLine());
        ArrayList<ArrayList<ArrayList<Integer>>> codebook = new ArrayList<>();
        for(int i = 0 ; i < codebookSize ; i++){
            ArrayList<ArrayList<Integer>> temp = new ArrayList<>();
            for(int j = 0 ; j < vectorHeight ; j++){
                ArrayList<Integer> temp2 = new ArrayList<>();
                String [] line = bufferedReader.readLine().split(" ");
                for(int k = 0 ; k < vectorWidth ; k++){
                    temp2.add(Integer.parseInt(line[k]));
                }
                temp.add(temp2);
            }
            codebook.add(temp);
        }
        ArrayList<ArrayList<ArrayList<Integer>>> decompressed = new ArrayList<>();
        String [] line = bufferedReader.readLine().split(" ");
        for(int i = 0 ; i < line.length ; i++){
            ArrayList<ArrayList<Integer>> temp = codebook.get(Integer.parseInt(line[i]));
            decompressed.add(temp);
        }

        bufferedReader.close();
        ArrayList<ArrayList<Integer>> pixels = new ArrayList<>();
        int width_vector = width / vectorWidth;
        int height_vector = height / vectorHeight;
        for(int x = 0 ; x < height ; x++){
            ArrayList<Integer> temp = new ArrayList<>();
            for(int y = 0 ; y < width ; y++){
                temp.add(decompressed.get((x / vectorHeight) * width_vector + (y / vectorWidth)).get(x % vectorHeight).get(y % vectorWidth));
            }
            pixels.add(temp);
        }
        imageHandler.writeImage(pixels , outputPath);

    }
}
