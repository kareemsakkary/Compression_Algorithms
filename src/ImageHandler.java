import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class ImageHandler {
    public static int[][][] readImage(String path) throws IOException {
        BufferedImage img = ImageIO.read(new File(path));
        int[][][] pixels = new int[img.getWidth()][img.getHeight()][3];
        for(int i = 0 ; i < img.getWidth() ; i++){
            for(int j = 0 ; j < img.getHeight() ; j++){
                int r = (img.getRGB(i , j) >> 16) & 0xff;
                int g = (img.getRGB(i , j) >> 8) & 0xff;
                int b = img.getRGB(i , j) & 0xff;
//                int gray = (r + g + b) / 3;
//                pixels[i][j] = gray;
                pixels[i][j][0] = r;
                pixels[i][j][1] = g;
                pixels[i][j][2] = b;
            }
        }
        return pixels;
    }
    public static void writeImage(int[][][] pixels, String path) throws IOException {
        BufferedImage img = new BufferedImage(pixels.length, pixels[0].length, BufferedImage.TYPE_INT_RGB);
        for(int i = 0 ; i < pixels.length ; i++){
            for(int j = 0 ; j < pixels[0].length ; j++){
//                int gray = pixels[i][j];
                int r = pixels[i][j][0];
                int g = pixels[i][j][1];
                int b = pixels[i][j][2];
                int rgb = (r << 16) | (g << 8) | b;
                img.setRGB(i , j , rgb);
            }
        }
        ImageIO.write(img, "jpg", new File(path));
    }
}
