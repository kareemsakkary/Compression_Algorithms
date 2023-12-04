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
                int a = 255;
                int r = (pixels[i][j] >> 16) & 0xff;
                int g = (pixels[i][j] >> 8) & 0xff;
                int b = pixels[i][j] & 0xff;
                int avg = (r + g + b) / 3;
                int p = (a << 24) | (avg << 16) | (avg << 8) | avg;
                img.setRGB(i , j , p);
            }
        }
        ImageIO.write(img , "jpg" , new File(path));
    }
}
