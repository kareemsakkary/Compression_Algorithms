import java.io.IOException;
import java.util.ArrayList;

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
    public ArrayList<ArrayList<Integer>> calculateAvg(ArrayList<ArrayList<ArrayList<Integer>>> vectors){
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
    public ArrayList<ArrayList<ArrayList<Integer>>> getCodesBookHelper(ArrayList<ArrayList<ArrayList<Integer>>> vectors,ArrayList<ArrayList<ArrayList<Integer>>> codebook){
        if(codebook.size() == codebookSize){
            return codebook;
        }
        ArrayList<ArrayList<ArrayList<Integer>>> newCodebook = new ArrayList<>();
        for(int i = 0 ; i < codebook.size() ; i++){
            ArrayList<ArrayList<Integer>> code1,code2;
            code1 = new ArrayList<>();
            code2 = new ArrayList<>();
            for(int x = 0 ; x < vectorHeight ; x++){
                ArrayList<Integer> temp1, temp2;
                temp1 = new ArrayList<>();
                temp2 = new ArrayList<>();
                for(int y = 0 ; y < vectorWidth ; y++){
                    temp1.add((int)Math.floor(codebook.get(i).get(x).get(y)));
                    temp2.add((int)Math.floor(codebook.get(i).get(x).get(y))+1);

                }
                code1.add(temp1);
                code2.add(temp2);
            }
            newCodebook.add(code1);
            newCodebook.add(code2);
        }
        ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> newGroups = new ArrayList<>();
        for(int i = 0 ; i < newCodebook.size() ; i++){
            ArrayList<ArrayList<ArrayList<Integer>>> group = new ArrayList<>();
            newGroups.add(group);
        }
        int[] groupIndex = new int[vectors.size()];
//        boolean flag = true;
//        while (flag){
            for(int i = 0 ; i < newCodebook.size() ; i++){
                ArrayList<ArrayList<ArrayList<Integer>>> group = new ArrayList<>();
                newGroups.set(i, group);
            }
//            flag = false;
            for(int i = 0 ; i < vectors.size() ; i++){
                double minError = Integer.MAX_VALUE;
                int minIndex = 0;
                for(int j = 0 ; j < newCodebook.size() ; j++){
                    double error = calculateError(vectors.get(i) , newCodebook.get(j));
                    if(error < minError){
                        minError = error;
                        minIndex = j;

                    }
                }
//                if(groupIndex[i] != minIndex){
//                    flag = true;
//                }
                groupIndex[i] = minIndex;
                newGroups.get(minIndex).add(vectors.get(i));
            }

            for(int i = 0; i < newCodebook.size() ; i++){
                if(!newGroups.get(i).isEmpty())
                    newCodebook.set(i,calculateAvg(newGroups.get(i)));
            }
//        }

        return getCodesBookHelper(vectors,newCodebook);
    }
    public ArrayList<ArrayList<ArrayList<Integer>>> getCodesBook(ArrayList<ArrayList<Integer>> pixels,int vectorWidth , int vectorHeight, int codebookSize){
        this.pixels = pixels;
        this.codebookSize = codebookSize;
        picHeight = pixels.size();
        picWidth = pixels.get(0).size();
        this.vectorWidth = vectorWidth -  (picWidth % vectorWidth);
        this.vectorHeight = vectorHeight - (picHeight % vectorHeight);

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
        ArrayList<ArrayList<ArrayList<Integer>>> codebook = getCodesBook(pixelsList , 10 , 10 , 1024);
        ArrayList<ArrayList<Integer>> compressed = new ArrayList<>();
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
            compressed.add(codebook.get(minIndex).get(0)); // add the codebook vector to compressed list
        }
        int [][] compressedImage = new int[compressed.size()][compressed.get(0).size()];
        for(int i = 0 ; i < compressed.size() ; i++){
            for(int j = 0 ; j < compressed.get(0).size() ; j++){
                compressedImage[i][j] = compressed.get(i).get(j);
            }
        }
        imageHandler.writeImage(compressedImage , outputPath);
    }

    @Override
    public void decompress(String inputPath, String outputPath) throws IOException {


    }
}
