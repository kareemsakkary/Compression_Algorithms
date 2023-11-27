import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class Decompress extends JFrame {
    private Main home;

    private JPanel DecompressPanel;
    String file;
    private JButton decompressButton;
    private JButton returnButton;
    private JLabel fileName;
    private JButton chooseFileButton;
    private JLabel file_name;
    private Algorithm algorithm;



    public void decompressFile() throws IOException {
        if(file == null)
            JOptionPane.showMessageDialog(null,"Please choose a file");
        else
            algorithm.decompress(file,file.substring(0,file.length()-4) + "_plaintext"+".txt");
    }


    public Decompress(Algorithm algorithm){
        super("Decompress Application");
        this.algorithm = algorithm;
        setContentPane(DecompressPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(new ImageIcon("D:\\Java-IntelliJ\\LZW_GUI\\icon.png").getImage());
        setSize(550, 380);
        setLocationRelativeTo(null);
        setVisible(true);
        home = new Main();
        home.setVisible(false);
        decompressButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    decompressFile();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                home.setVisible(true);
            }
        });
        chooseFileButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.showOpenDialog(DecompressPanel);
                file = fc.getSelectedFile().getAbsolutePath();
                file_name.setText(file);
                super.mouseClicked(e);
            }
        });
    }

}
