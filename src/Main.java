import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame {

    private Compress c;

    private Decompress d;
    private JPanel MainPanel;
    private JButton compressButton;
    private JButton decompressButton;
    private JComboBox comboBox1;

    private Algorithm algorithm;
    public Main(){
        super("Compressor-Algorithms Application");
        setContentPane(MainPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(new ImageIcon("D:\\Java-IntelliJ\\LZW_GUI\\icon.png").getImage());
        setSize(550, 380);
        setLocationRelativeTo(null);
        setVisible(true);
        algorithm = null;
        comboBox1.addItem("Choose an Algorithm");
        comboBox1.addItem("LZ77");
        comboBox1.addItem("LZW");
        comboBox1.addItem("Huffman");
        comboBox1.addItem("Vector Quantization");

        comboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(comboBox1.getSelectedItem().equals("LZW")){
                    algorithm = new LZW();
                }
                else if(comboBox1.getSelectedItem().equals("LZ77")){
                    algorithm = new LZ77();
                }
                else if(comboBox1.getSelectedItem().equals("Huffman")){
                    algorithm = new Huffman();
                }else if(comboBox1.getSelectedItem().equals("Vector Quantization")){
                    algorithm = new VectorQuantization();
                } else{
                    algorithm = null;
                }
            }
        });
        compressButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(algorithm == null){
                    JOptionPane.showMessageDialog(null, "Please choose an algorithm first", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                c = new Compress(algorithm);
                setVisible(false);
            }
        });
        decompressButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(algorithm == null){
                    JOptionPane.showMessageDialog(null, "Please choose an algorithm first", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                d = new Decompress(algorithm);
                setVisible(false);
            }
        });
    }

    public static void main(String[] args) {
        new Main();
    }

}
