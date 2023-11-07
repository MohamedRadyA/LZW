import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static ArrayList<Integer> compressLZW(String str) {
        // Add all ASCII characters to the dictionary
        Map<String, Integer> dict = new HashMap<>();
        for (char c = 0; c < 128; c++)
            dict.put(String.valueOf(c), dict.size());

        ArrayList<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < str.length(); i++) { // For each starting character
            String longestFoundInDict = "";
            int j = i;
            for (; j < str.length(); j++) { // Find the longest string that is in the dictionary
                String current = longestFoundInDict + str.charAt(j);
                if (dict.containsKey(current)) {
                    longestFoundInDict = current; // Update the longest string found
                } else {
                    dict.put(current, dict.size()); // Add the first non-found string to the dictionary
                    break;
                }
            }
            // Compress the longest string found, e.g. "A" -> "<65>"
            result.add(dict.get(longestFoundInDict));
            i = j - 1;
        }
        return result;
    }

    public static String decompressLZW(ArrayList<Integer> arr) {
        // Add all ASCII characters to the dictionary
        Map<Integer, String> dict = new HashMap<>();
        for (char c = 0; c < 128; c++)
            dict.put(dict.size(), String.valueOf(c));

        String last = "";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < arr.size(); i++) {
            if (dict.containsKey(arr.get(i))) {
                String current = dict.get(arr.get(i));
                result.append(current);
                if (i > 0) dict.put(dict.size(), last + current.charAt(0));
                last = current;
            } else {
                last = last + last.charAt(0);
                dict.put(dict.size(), last);
                result.append(last);
            }
        }
        return result.toString();
    }

    public static void main(String[] args) {
        String str = "ABBBBABBBBBBAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBBBBAAAAAAAAAAAAAAABABABABABABABABABABABABABABABABBBBBBBBBBBAAAAAAAAAA";
        ArrayList<Integer> compressed = compressLZW(str);
        String decompressed = decompressLZW(compressed);
//        Compress example:
//        Parser.writeToBinFile("compressed.bin", compressed);
//        Parser.writeToTxtFile("decompressed.txt", decompressed);
//        Parser.writeToTxtFile("decompressed.txt", decompressLZW(Parser.readFromBinFile("compressed.bin")));
    }
}