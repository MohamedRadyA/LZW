import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.IOException;

public class MainFrame extends JFrame {
    public MainFrame() {
        super("LZW Compressor");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JTextPane filePane = new JTextPane();
        filePane.setBackground(Color.lightGray);
        filePane.setEditable(false);
        filePane.setFont(new Font("Arial", Font.PLAIN, 20));
        add(filePane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BorderLayout());

        southPanel.add(inputPanel, BorderLayout.CENTER);

        JTextField pathField = new JTextField();
        pathField.setEditable(false);

        JButton selectButton = new JButton("Select");
        selectButton.setSize(400, 40);
        selectButton.setFocusPainted(false);

        inputPanel.add(pathField, BorderLayout.CENTER);
        inputPanel.add(selectButton, BorderLayout.EAST);

        JPanel buttonPanel = new JPanel();
        JButton compressButton = new JButton("Compress");
        compressButton.setFocusPainted(false);

        JButton decompressButton = new JButton("Decompress");
        decompressButton.setFocusPainted(false);
        buttonPanel.add(compressButton, BorderLayout.WEST);
        buttonPanel.add(decompressButton, BorderLayout.EAST);

        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        selectButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.showDialog(compressButton, "Select File");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (fileChooser.getSelectedFile() == null) return;
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            pathField.setText(path);
            filePane.setText(Parser.readFromTxtFile(path));
        });

        compressButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.showDialog(compressButton, "Save Compressed File");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (pathField.getText().isEmpty())
                return;
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            Parser.writeBytesToBinFile(path, Huffman.compressHuffman(Parser.readFromTxtFile(pathField.getText())));
            JOptionPane.showMessageDialog(inputPanel, "Compressed Successfully!");
        });

        decompressButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.showDialog(decompressButton, "Save Decompressed File");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (pathField.getText().isEmpty())
                return;
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            try {
                Parser.writeToTxtFile(path, Huffman.decode(Parser.readBytesFromBinFile(pathField.getText())));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            JOptionPane.showMessageDialog(inputPanel, "Decompressed Successfully!");
        });


        add(southPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    public static void main(String[] args) {
        UIManager.put("Button.font", new FontUIResource(new Font("Helvetica", Font.BOLD, 20)));
        new MainFrame();
    }
}
