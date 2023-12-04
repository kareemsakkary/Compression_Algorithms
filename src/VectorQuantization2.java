import java.io.IOException;
import java.util.ArrayList;

public class VectorQuantization2 {
    int[][] pixels;
    int picWidth , picHeight;
    int vectorWidth , vectorHeight;
    int codebookSize;
    public VectorQuantization2(String path,int vectorWidth , int vectorHeight, int codebookSize) throws IOException {
        pixels = ImageHandler.readImage(path);
        picHeight = pixels.length;
        picWidth = pixels[0].length;
        this.vectorWidth = vectorWidth; // the small block width
        this.vectorHeight = vectorHeight; // the small block height
        this.codebookSize = codebookSize; // the number of blocks in the codebook
    }
    public ArrayList<ArrayList<ArrayList<Integer>>> getVectors(){ // get the small blocks from the image
        ArrayList<ArrayList<ArrayList<Integer>>>  vectors = new ArrayList<>(); // the small blocks
        for(int i = 0 ; i < picHeight ; i += vectorHeight){ // loop over the image height
            for(int j = 0 ; j < picWidth ; j += vectorWidth){ // loop over the image width
                ArrayList<ArrayList<Integer>> vector = new ArrayList<>(); // the small block
                for(int k = i ; k < i + vectorHeight ; k++){ // loop over the small block height
                    ArrayList<Integer> temp = new ArrayList<>(); // the small block row
                    for(int l = j ; l < j + vectorWidth ; l++){ // loop over the small block width
                        temp.add(pixels[k][l]); // add the pixel to the small block row
                    }
                    vector.add(temp); // add the small block row to the small block
                }
                vectors.add(vector); // add the small block to the small blocks
            }
        }
        return vectors; // return the small blocks
    }
    public ArrayList<ArrayList<Integer>> calculateAvg(ArrayList<ArrayList<ArrayList<Integer>>> vectors){ // calculate the average of the small blocks
        ArrayList<ArrayList<Integer>> avg = new ArrayList<>(); // the average of the small blocks
        for(int i = 0 ; i < vectors.get(0).size() ; i++){ // loop over the small block height
            ArrayList<Integer> temp = new ArrayList<>(); // the average of the small block row
            for(int j = 0 ; j < vectors.get(0).get(0).size() ; j++){ // loop over the small block width
                double sum = 0; // the sum of the small block row
                for(int k = 0 ; k < vectors.size() ; k++){ // loop over the small blocks
                    sum += vectors.get(k).get(i).get(j); // add the pixel to the sum
                }
                temp.add((int) (sum / (vectors.size()))); // add the average of the small block row to the average of the small block
            }
            avg.add(temp); // add the average of the small block to the average of the small blocks
        }
        return avg; // return the average of the small blocks
    }

    public double calculateError(ArrayList<ArrayList<Integer>> vector1 , ArrayList<ArrayList<Integer>> vector2){ // calculate the error between two small blocks
        double error = 0; // the error between the two small blocks
        for(int i = 0 ; i < vector1.size() ; i++){ // loop over the small block height
            for(int j = 0 ; j < vector1.get(0).size() ; j++){ // loop over the small block width
                error += Math.pow(vector1.get(i).get(j) - vector2.get(i).get(j) , 2); // add the error between the two pixels to the error
            }
        }
        return error; // return the error between the two small blocks
    }
    // get codebook
    public static ArrayList<ArrayList<ArrayList<Integer>>> getCodebook(ArrayList<ArrayList<ArrayList<Integer>>> vectors , ArrayList<ArrayList<Integer>> avg , int codebookSize){ // get the codebook
        return null;
    }
    public static void compress(String path , int vectorWidth , int vectorHeight , int codebookSize) throws IOException {
        VectorQuantization2 vq = new VectorQuantization2(path , vectorWidth , vectorHeight , codebookSize); // create a new VectorQuantization2 object
        ArrayList<ArrayList<ArrayList<Integer>>> vectors = vq.getVectors(); // get the small blocks from the image
        ArrayList<ArrayList<Integer>> avg = vq.calculateAvg(vectors); // calculate the average of the small blocks
        ArrayList<ArrayList<ArrayList<Integer>>> codebook = getCodebook(vectors , avg , codebookSize); // get the codebook
        ArrayList<Integer> indexes = new ArrayList<>(); // the indexes of the small blocks in the codebook
        for(int i = 0 ; i < vectors.size() ; i++){ // loop over the small blocks
            double minError = Double.MAX_VALUE; // the minimum error between the small block and the average of the small blocks
            int index = 0; // the index of the small block in the codebook
            for(int j = 0 ; j < codebook.size() ; j++){ // loop over the codebook
                double error = vq.calculateError(vectors.get(i) , codebook.get(j)); // calculate the error between the small block and the average of the small blocks
                if(error < minError){ // if the error is less than the minimum error
                    minError = error; // update the minimum error
                    index = j; // update the index
                }
            }
            indexes.add(index); // add the index to the indexes
        }
        int[][] compressed = new int[vq.picHeight][vq.picWidth]; // the compressed image
        int index = 0; // the index of the small block in the indexes
        for(int i = 0 ; i < vq.picHeight ; i += vectorHeight){ // loop over the image height
            for(int j = 0 ; j < vq.picWidth ; j += vectorWidth){ // loop over the image width
                for(int k = i ; k < i + vectorHeight ; k++){ // loop over the small block height
                    for(int l = j ; l < j + vectorWidth ; l++){ // loop over the small block width
                        compressed[k][l] = codebook.get(indexes.get(index)).get(k - i).get(l - j); // add the pixel to the compressed image
                    }
                }
                index++; // update the index
            }
        }
        ImageHandler.writeImage(compressed , "compressed.jpg"); // write the compressed image
    }
}
