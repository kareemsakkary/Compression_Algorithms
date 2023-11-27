import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame {

    private Compress c;

    private Decompress d;
    private JPanel MainPanel;
    private JButton compressButton;
    private JButton decompressButton;
    private JComboBox comboBox1;

    private Algorithm algorithm;
    public Main(){
        super("LZW-Algorithm Application");
        setContentPane(MainPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(new ImageIcon("D:\\Java-IntelliJ\\LZW_GUI\\icon.png").getImage());
        setSize(550, 380);
        setLocationRelativeTo(null);
        setVisible(true);
        comboBox1.addItem("LZ77-Algorithm");
        comboBox1.addItem("LZW-Algorithm");
        comboBox1.addItem("Huffman");

        comboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(comboBox1.getSelectedItem().equals("LZW-Algorithm")){
                    algorithm = new LZW();
                }
                else if(comboBox1.getSelectedItem().equals("LZ77-Algorithm")){
                    algorithm = new LZ77();
                }
                else if(comboBox1.getSelectedItem().equals("Huffman")){
                    algorithm = new Huffman();
                }
            }
        });
        compressButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                c = new Compress(algorithm);
                setVisible(false);
            }
        });
        decompressButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                d = new Decompress(algorithm);
                setVisible(false);
            }
        });
    }

    public static void main(String[] args) {
        new Main();
    }

}
