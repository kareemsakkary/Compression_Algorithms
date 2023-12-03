import java.util.ArrayList;

public class VectorQuantization {
    ArrayList<ArrayList<Double>> pixels;
    int picWidth , picHeight;
    int vectorWidth , vectorHeight;
    int codebookSize;
    public VectorQuantization(ArrayList<ArrayList<Double>> pixels,int vectorWidth , int vectorHeight, int codebookSize) {
        this.pixels = pixels;
        picHeight = pixels.size();
        picWidth = pixels.get(0).size();
        this.vectorWidth = vectorWidth;
        this.vectorHeight = vectorHeight;
        this.codebookSize = codebookSize;
    }

    public ArrayList<ArrayList<ArrayList<Double>>> getVectors(){
        ArrayList<ArrayList<ArrayList<Double>>>  vectors = new ArrayList<>();
        for(int i = 0 ; i < picHeight ; i += vectorHeight){
            for(int j = 0 ; j < picWidth ; j += vectorWidth){
                ArrayList<ArrayList<Double>> vector = new ArrayList<>();
                for(int k = i ; k < i + vectorHeight ; k++){
                    ArrayList<Double> temp = new ArrayList<>();
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
    public ArrayList<ArrayList<Double>> calculateAvg(ArrayList<ArrayList<ArrayList<Double>>> vectors){
        ArrayList<ArrayList<Double>> avg = new ArrayList<>();
        for(int i = 0 ; i < vectors.get(0).size() ; i++){
            ArrayList<Double> temp = new ArrayList<>();
            for(int j = 0 ; j < vectors.get(0).get(0).size() ; j++){
                double sum = 0;
                for(int k = 0 ; k < vectors.size() ; k++){
                    sum += vectors.get(k).get(j).get(k);
                }
                temp.add(sum / (vectors.size()));
            }
            avg.add(temp);
        }
        return avg;
    }
    
    public double calculateError(ArrayList<ArrayList<Double>> vector1 , ArrayList<ArrayList<Double>> vector2){
        double error = 0;
        for(int i = 0 ; i < vector1.size() ; i++){
            for(int j = 0 ; j < vector1.get(0).size() ; j++){
                error += Math.pow(vector1.get(i).get(j) - vector2.get(i).get(j) , 2);
            }
        }
        return error;
    }
    public ArrayList<ArrayList<ArrayList<Double>>> compressHelper(ArrayList<ArrayList<ArrayList<Double>>> vectors,ArrayList<ArrayList<ArrayList<Double>>> codebook,ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> groups){
        if(codebook.size() == codebookSize){
            return codebook;
        }
        ArrayList<ArrayList<ArrayList<Double>>> newCodebook = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> newGroups = new ArrayList<>();

        return compressHelper(vectors,newCodebook,newGroups);
    }
    public ArrayList<ArrayList<ArrayList<Double>>> compress(){
        ArrayList<ArrayList<ArrayList<Double>>> vectors = getVectors();
        ArrayList<ArrayList<Double>> avg = calculateAvg(vectors);
        ArrayList<ArrayList<ArrayList<Double>>> codebook = new ArrayList<>();
        codebook.add(avg);
        ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> groups = new ArrayList<>();
        groups.add(vectors);
        return compressHelper(vectors,codebook,groups);
    }

    
}
