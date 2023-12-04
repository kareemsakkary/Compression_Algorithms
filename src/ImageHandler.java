import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class ImageHandler {
    public static int[][] readImage(String path) throws IOException {
        BufferedImage img = ImageIO.read(new File(path));
        int[][] pixels = new int[img.getWidth()][img.getHeight()];
        for(int i = 0 ; i < img.getWidth() ; i++){
            for(int j = 0 ; j < img.getHeight() ; j++){
                int r = (img.getRGB(i , j) >> 16) & 0xff;
                int g = (img.getRGB(i , j) >> 8) & 0xff;
                int b = img.getRGB(i , j) & 0xff;
                int gray = (r + g + b) / 3;
                pixels[i][j] = gray;
            }
        }
        return pixels;
    }
    public static void writeImage(ArrayList<ArrayList<Integer>>pixels, String path) throws IOException {
        BufferedImage img = new BufferedImage(pixels.size(), pixels.get(0).size(), BufferedImage.TYPE_BYTE_GRAY);
        for(int i = 0 ; i < pixels.size() ; i++){
            for(int j = 0 ; j < pixels.get(0).size() ; j++){
                int gray = pixels.get(i).get(j);
                int rgb = (gray << 16) | (gray << 8) | gray;
                img.setRGB(i , j , rgb);
            }
        }
        ImageIO.write(img, "jpg", new File(path));
    }
}
