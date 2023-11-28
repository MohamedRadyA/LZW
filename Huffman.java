import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

class HuffmanNode {
    char data;
    int freq;
    HuffmanNode left;
    HuffmanNode right;
    HuffmanNode() {
        left = null;
        right = null;
    }
}
public class Huffman {
    public static byte[] compressHuffman(String str) {
        int[] freq = new int[256];
        for (int i = 0; i < str.length(); i++)
            freq[str.charAt(i)]++;
        HuffmanNode root = buildTree(freq);
        String[] codes = new String[256];
        encode(root, "", codes);


        ArrayList<Byte> bytes = new ArrayList<>();

        // get the compressed data
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++)
            result.append(codes[str.charAt(i)]);
        bytes.add((byte) ((8 - (result.length() % 8)) % 8)); // add the number of padding bits to the compressed overhead
        if (result.length() % 8 != 0) {
            String s = "";
            for (int i = 0; i < 8 - result.length() % 8; i++) {
                s += '0';
            }
            result.append(s); // pad the result with 0s to make it divisible by 8

        }

        // Add overhead to the result
        for (int i = 0; i < 256; i++) {
            if (freq[i] > 0) {
                // put the character, code, code length
                bytes.add((byte) i);
                bytes.add((byte) codes[i].length());
                bytes.add((byte) Integer.parseInt(codes[i], 2));
            }
        }

        // add separator between overhead and compressed data
        bytes.add((byte) 0);

        // convert the binary string to bytes (each byte is written in text file as a character)
        for (int i = 0; i < result.length(); i += 8) {
            String byteStr = result.substring(i, Math.min(i + 8, result.length()));
            bytes.add ((byte) Integer.parseInt(byteStr, 2));
        }

        byte[] resultBytes = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++)
            resultBytes[i] = bytes.get(i);

        return resultBytes;
    }
    public static String decode(byte[] bytes) {
        int padding = bytes[0];
        HashMap<String, Character> codes = new HashMap<>();
        String curCode = "";
        int i = 1;
        for (; i < bytes.length; i+=3) {
            if (bytes[i] == 0) {
                i++;
                break;
            }
            Character c = (char) bytes[i];
            int codeLength = bytes[i + 1];
            int code = bytes[i + 2];
            String codeStr = Integer.toBinaryString(code);
            codeStr = codeStr.substring(codeStr.length() - codeLength);
            codes.put(codeStr, c);
        }
        StringBuilder restCodes = new StringBuilder();
        for (; i < bytes.length; i++) {
            // Get the binary representation of the byte (last 8 bits after converting to binary representation of int)
            String byteStr = Integer.toBinaryString(bytes[i]).substring(24);
            restCodes.append(byteStr);
        }
        StringBuilder result = new StringBuilder();
        for (int j = 0; j < restCodes.length() - padding; j++) {
            curCode += restCodes.charAt(j);
            if (codes.containsKey(curCode)) {
                result.append(codes.get(curCode));
                curCode = "";
            }
        }
        return result.toString();
    }

    private static void encode(HuffmanNode root, String s, String[] codes) {
        if (root == null) return;
        if (root.left == null && root.right == null) {
            codes[root.data] = s;
            return;
        }
        encode(root.left, s + "0", codes);
        encode(root.right, s + "1", codes);
    }

    private static HuffmanNode buildTree(int[] freq) {
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a.freq));
        for (int i = 0; i < 256; i++) {
            if (freq[i] > 0) {
                HuffmanNode node = new HuffmanNode();
                node.data = (char) i;
                node.freq = freq[i];
                pq.add(node);
            }
        }
        while (pq.size() > 1) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();
            assert right != null;
            HuffmanNode parent = new HuffmanNode();
            parent.freq = left.freq + right.freq;
            parent.left = left;
            parent.right = right;
            pq.add(parent);
        }
        return pq.poll();
    }

    public static void main(String[] args) throws IOException {
        String str = "abcdaabb"; // 110100101111100
//        for (byte b : compressHuffman(str))
//            System.out.print((char)b);
        Parser.writeBytesToBinFile("compressed.bin", compressHuffman(str));
        byte[] bytes = Parser.readBytesFromBinFile("compressed.bin");
        Parser.writeToTxtFile("decompressed.txt", decode(bytes));
    }
}