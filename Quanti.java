import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

class Vector {

    int width, height;

    int data[][];

    public Vector(int width, int height, int[][] data) {
        this.width = width;
        this.height = height;
        this.data = data;
    }

    public Vector(int width, int height) {
        this.width = width;
        this.height = height;
        this.data = new int[width][height];
    }

    public boolean equals(Vector x) {
        if (x.width != width) return false;
        if (x.height != height) return false;
        for (int i = 0; i < x.width; i++) {
            for (int j = 0; j < x.height; j++) {
                if (x.data[i][j] != data[i][j]) return false;
            }
        }
        return true;
    }

}

public class Quanti {

    public static double getDistance(Vector x, Vector y) {
        double sum = 0;
        for (int i = 0; i < x.width; i++) {
            for (int j = 0; j < x.height; j++) {
                sum += Math.abs(x.data[i][j] - y.data[i][j]);
            }
        }
        return sum;
    }

    public static Vector getAverage(ArrayList<Vector> data) {
        Vector avg = new Vector(data.get(0).width, data.get(0).height);
        for (Vector x : data) {
            for (int i = 0; i < x.height; i++) {
                for (int j = 0; j < x.width; j++) {
                    avg.data[i][j] += x.data[i][j];
                }
            }
        }
        for (int i = 0; i < avg.height; i++) {
            for (int j = 0; j < avg.width; j++) {
                avg.data[i][j] /= data.size();
            }
        }
        return avg;
    }

    public static Vector getLeftVector(Vector avg) {
        Vector left = new Vector(avg.width, avg.height);
        for (int i = 0; i < avg.width; i++) {
            for (int j = 0; j < avg.height; j++) {
                left.data[i][j] = (int)Math.ceil(avg.data[i][j] - 1);
            }
        }
        return left;
    }

    public static Vector getRightVector(Vector avg) {
        Vector right = new Vector(avg.width, avg.height);
        for (int i = 0; i < avg.width; i++) {
            for (int j = 0; j < avg.height; j++) {
                right.data[i][j] = (int)Math.floor(avg.data[i][j] + 1);
            }
        }
        return right;
    }

    public static void LBG(int leafCount, ArrayList<Vector> currentDataset, ArrayList<ArrayList<Vector>> leaves) {
        if (leafCount == 1 || currentDataset.isEmpty()) {
            if (!currentDataset.isEmpty())
                leaves.add(currentDataset);
            return;
        }

        ArrayList<Vector> leftVectors = new ArrayList<>();
        ArrayList<Vector> rightVectors = new ArrayList<>();

        Vector avg = getAverage(currentDataset);
        Vector leftVector = getLeftVector(avg);
        Vector rightVector = getRightVector(avg);
        for (Vector x : currentDataset) {
            double left = getDistance(x, leftVector);
            double right = getDistance(x, rightVector);
            if (left <= right) {
                leftVectors.add(x);
            } else {
                rightVectors.add(x);
            }
        }
        LBG(leafCount / 2, leftVectors, leaves);
        LBG(leafCount / 2, rightVectors, leaves);
    }

    public static ArrayList<Vector> splitImage(int[][] image, int vecWidth, int vecHeight) {
        ArrayList<Vector> imageBlocks = new ArrayList<>();
        for (int i = 0; i < image.length; i += vecWidth) {
            for (int j = 0; j < image[0].length; j += vecHeight) {
                Vector block = new Vector(vecWidth, vecHeight);
                for (int k = 0; k < vecWidth; k++) {
                    for (int l = 0; l < vecHeight; l++) {
                        block.data[k][l] = image[Math.min(i + k, image.length - 1)][Math.min(j + l, image[0].length - 1)];
                    }
                }
                imageBlocks.add(block);
            }
        }
        return imageBlocks;
    }

    public static ArrayList<Vector> redistribute(ArrayList<Vector> imageBlocks, ArrayList<ArrayList<Vector>> lbg) {
        ArrayList<Vector> currentAverages = new ArrayList<>();
        for (ArrayList<Vector> x : lbg) {
            currentAverages.add(getAverage(x));
        }

        ArrayList[] newLbg = new ArrayList[currentAverages.size()];
        while (true) {
            for (int i = 0; i < currentAverages.size(); i++)
                newLbg[i] = new ArrayList<>();

            for (Vector x : imageBlocks) {
                int minDistIndex = getMinDist(currentAverages, x);
                newLbg[minDistIndex].add(x);
            }
            boolean same = true;
            for (int i = 0; i < currentAverages.size(); i++) {
                Vector newAvg = getAverage(newLbg[i]);
                if (!currentAverages.get(i).equals(newAvg)) {
                    same = false;
                    currentAverages.set(i, newAvg);
                }
            }
            if (same) break;
        }
        return currentAverages;
    }

    public static ArrayList<Integer> getCompressedCodes(ArrayList<Vector> imageBlocks, ArrayList<Vector> codeBook) {
        ArrayList<Integer> compressed = new ArrayList<>();
        for (Vector x : imageBlocks) {
            int minDistIndex = getMinDist(codeBook, x);
            compressed.add(minDistIndex);
        }
        return compressed;
    }

    private static int getMinDist(ArrayList<Vector> codeBook, Vector x) {
        int minDistIndex = -1;
        double minDist = 1e9;
        for (int i = 0; i < codeBook.size(); i++) {
            double dist = getDistance(x, codeBook.get(i));
            if (dist < minDist) {
                minDist = dist;
                minDistIndex = i;
            }
        }
        return minDistIndex;
    }

    static public String read(String path) {
        StringBuilder str = new StringBuilder();
        try {
            FileInputStream read = new FileInputStream(path);
            InputStreamReader stream_reader = new InputStreamReader(read, StandardCharsets.UTF_8);

            int byt;
            while ((byt = stream_reader.read()) != -1) {
                str.append((char) byt);
            }

            stream_reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }

    static void write(String content, String path) {
        try {
            FileOutputStream fileOut = new FileOutputStream(path);
            OutputStreamWriter writer = new OutputStreamWriter(fileOut, StandardCharsets.UTF_8);

            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void saveImage(String fileName, int[][] image, int height, int width) {
        ImageRW.writeImage(image, width, height, fileName);
    }

    public static void compress(int[][] image, int codeBookSize, int vectorWidth, int vectorHeight) {
        ArrayList<Vector> imageBlocks = splitImage(image, vectorWidth, vectorHeight);
        ArrayList<ArrayList<Vector>> lbg = new ArrayList<>();
        LBG(codeBookSize, imageBlocks, lbg);
        ArrayList<Vector> codeBook = redistribute(imageBlocks, lbg);
        ArrayList<Integer> compressedImage = getCompressedCodes(imageBlocks, codeBook);


        StringBuilder str = new StringBuilder();

        // original height and width
        str.append((char) image.length);
        str.append((char) image[0].length);

        // vector height and width
        str.append((char) vectorHeight);
        str.append((char) vectorWidth);

        // vector indices of image
        for (Integer index : compressedImage) {
            str.append((char) index.intValue());
        }

        // quantized vectors
        for (Vector vector : codeBook) {
            for (int i = 0; i < vector.width; i++) {
                for (int j = 0; j < vector.height; j++) {
                    str.append((char) vector.data[i][j]);
                }
            }
        }
        write(String.valueOf(str), "comp.bin");
    }

    static void decompress(String fileName) {
        String str = read(fileName);

        int originalHeight = str.charAt(0);
        int originalWidth = str.charAt(1);
        int vectorHeight = str.charAt(2);
        int vectorWidth = str.charAt(3);
        int scaledHeight = ((originalHeight + vectorHeight - 1) / vectorHeight) * vectorHeight;
        int scaledWidth = ((originalWidth + vectorWidth - 1) / vectorWidth) * vectorWidth;

        int numberOfVectors = scaledHeight * scaledWidth / (vectorHeight * vectorWidth);
        ArrayList<Integer> compressedImage = new ArrayList<>();

        for (int i = 4; i < 4 + numberOfVectors; i++) {
            compressedImage.add((int) str.charAt(i));
        }

        int sizeOfVector = vectorHeight * vectorWidth;
        ArrayList<Vector> codeBook = new ArrayList<>();
        for (int i = 4 + numberOfVectors; i < str.length(); i += sizeOfVector) {
            int current = i;
            Vector vector = new Vector(vectorWidth, vectorHeight);
            for (int j = 0; j < vectorWidth; j++) {
                for (int k = 0; k < vectorHeight; k++) {
                    vector.data[j][k] = (int) str.charAt(current++);
                }
            }
            codeBook.add(vector);
        }

        int[][] image = new int[scaledHeight][scaledWidth];

        for (int i = 0; i < compressedImage.size(); i++) {
            int numberOfVectorsInRow = scaledWidth / vectorWidth;
            // top left cell in vector
            int x = i / numberOfVectorsInRow * vectorHeight;
            int y = i % numberOfVectorsInRow * vectorWidth;
            int code = compressedImage.get(i);
            for (int j = x; j < x + vectorWidth; j++) {
                for (int k = y; k < y + vectorHeight; k++) {
                    image[j][k] = (int) codeBook.get(code).data[j - x][k - y];
                }
            }
        }
        saveImage("decomp.jpg", image, originalHeight, originalWidth);
    }


    public static void compress(String path) {

        compress(ImageRW.readImage("pic6.jpg"), 16, 2, 2);
    }

    public static void main(String[] args) throws IOException {
        decompress("comp.bin");
    }

}
