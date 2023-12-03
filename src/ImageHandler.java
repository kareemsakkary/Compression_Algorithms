import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
public class ImageHandler {
    public static int[][] readImage(String path) throws IOException {
        BufferedImage img = ImageIO.read(new File(path));
        int[][] pixels = new int[img.getWidth()][img.getHeight()];
        for(int i = 0 ; i < img.getWidth() ; i++){
            for(int j = 0 ; j < img.getHeight() ; j++){
                pixels[i][j] = img.getRGB(i , j);
            }
        }
        return pixels;
    }
    public static void writeImage(int[][] pixels , String path) throws IOException {
        BufferedImage img = new BufferedImage(pixels.length , pixels[0].length , BufferedImage.TYPE_INT_RGB);
        for(int i = 0 ; i < pixels.length ; i++){
            for(int j = 0 ; j < pixels[0].length ; j++){
                img.setRGB(i , j , pixels[i][j]);
            }
        }
        ImageIO.write(img , "jpg" , new File(path));
    }
}