import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageHandler2d {
    public static int[][] readImage(String path) throws IOException {
        BufferedImage img = ImageIO.read(new File(path));
        int[][] pixels = new int[img.getWidth()][img.getHeight()];
        for(int i = 0 ; i < img.getWidth() ; i++){ // for each row
            for(int j = 0 ; j < img.getHeight() ; j++){
               int r = (img.getRGB(i, j) >> 16) & 0xFF; // get the red value
                int g = (img.getRGB(i, j) >> 8) & 0xFF; // get the green value
                int b = img.getRGB(i, j) & 0xFF; // get the blue value
                // convert to gray
                int gray = (r + g + b) / 3;
                pixels[i][j] = gray;
            }
        }
        return pixels;
    }
    public static void writeImage(int[][] pixels, String path) throws IOException {
        BufferedImage img = new BufferedImage(pixels.length, pixels[0].length, BufferedImage.TYPE_BYTE_GRAY);
        for(int i = 0 ; i < pixels.length ; i++){ // for each row
            for(int j = 0 ; j < pixels[0].length ; j++){
                // gray
                int gray = pixels[i][j];
                // write in gray scale
                int rgb = (gray << 16) | (gray << 8) | gray;
                img.setRGB(i, j, rgb);
            }
        }
        ImageIO.write(img, "jpg", new File(path));
    }
}
