import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

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
        else{
            // ask user for the path to save the compressed file
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
            fc.showSaveDialog(DecompressPanel);
            String path = fc.getSelectedFile().getAbsolutePath();
            // compress the file
            try {
                HashMap<String,Integer> required = new HashMap<>();
                algorithm.decompress(file , path,  required);
                JOptionPane.showMessageDialog(null,"File decompressed successfully");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,"Error while decompressing the file");
            }
        }
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
                fc.setCurrentDirectory(new File(System.getProperty("user.dir")));

                fc.showOpenDialog(DecompressPanel);
                file = fc.getSelectedFile().getAbsolutePath();
                file_name.setText(file);
                super.mouseClicked(e);
            }
        });
    }

}
