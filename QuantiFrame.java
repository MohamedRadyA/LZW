import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class QuantiFrame extends JFrame {
    public QuantiFrame() {
        super("Quanti Compressor");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel image = new JLabel();
        BufferedImage compressedImage;
        JScrollPane imageHolder = new JScrollPane();
        imageHolder.setBackground(Color.lightGray);
        add(imageHolder, BorderLayout.CENTER);

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

            try {
                image.setIcon(new ImageIcon(ImageIO.read(new File(path))));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            image.setHorizontalAlignment(JLabel.CENTER);
            imageHolder.getViewport().add(image);
        });

        compressButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.showDialog(compressButton, "Save Compressed File");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (pathField.getText().isEmpty())
                return;
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            Quanti.compress("");
            //Parser.writeBytesToBinFile(path, Huffman.compressHuffman(Parser.readFromTxtFile(pathField.getText())));
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
        new QuantiFrame();
    }
}