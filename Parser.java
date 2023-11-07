import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.nio.file.Files;

public class Parser {
    public static void writeToBinFile(String path, ArrayList<Integer> result) {
        File file = new File(path);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            for (Integer x : result)
                fos.write(x.byteValue());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToTxtFile(String path, String result) {
        try (FileWriter myWriter = new FileWriter(path)) {
            myWriter.write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFromTxtFile(String path) {
        String str = "";
        try {
            str = new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static ArrayList<Integer> readFromBinFile(String path) {
        File file = new File(path);
        ArrayList<Integer> compressed = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(file)) {
            int content;
            while ((content = fis.read()) != -1) {
                compressed.add(content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return compressed;
    }
}
