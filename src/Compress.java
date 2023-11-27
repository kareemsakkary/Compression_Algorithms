import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class Compress extends JFrame {

    private Main home;
    private JPanel CompressPanel;
    private JButton returnButton;
    private JButton compressButton;
    private JLabel fileLabel;
    private JButton chooseFileButton;
    private Algorithm algorithm;
    String file;
    public void compressFile() throws IOException {
        if(file == null)
            JOptionPane.showMessageDialog(null,"Please choose a file");
        else
            algorithm.compress(file,file.substring(0,file.length()-4) + "_" +algorithm.getAlgorithmName()+".txt");
    }

    public Compress(Algorithm algorithm){
        super("Compress Application");
        this.algorithm = algorithm;
        setContentPane(CompressPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(new ImageIcon("D:\\Java-IntelliJ\\LZW_GUI\\icon.png").getImage());
        setSize(550, 380);
        setLocationRelativeTo(null);
        setVisible(true);
        home = new Main();
        home.setVisible(false);
        compressButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    compressFile();
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
                fc.showOpenDialog(CompressPanel);
                file = fc.getSelectedFile().getAbsolutePath();
                fileLabel.setText(file);
                super.mouseClicked(e);
            }
        });
    }

}
