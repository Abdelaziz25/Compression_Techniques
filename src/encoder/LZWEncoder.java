package encoder;

import java.util.*;
import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;

public class LZWEncoder {
    private void buildAsciiTable(HashMap<String, Integer> dictionary) {
        for (int i = 0; i <= 255; i++) {
            dictionary.put(String.valueOf((char) i), i);
        }
    }

    public void encode(String inputPath, int maxSize) {
        try {
            // Read input
            String input = new String(Files.readAllBytes(Paths.get(inputPath + ".txt")), StandardCharsets.UTF_8);

            // Initialize output
            StringBuilder outputFile = new StringBuilder();

            // Initialize ASCII dictionary
            HashMap<String, Integer> dictionary = new HashMap<String, Integer>();
            buildAsciiTable(dictionary);

            StringBuilder curString = new StringBuilder();
            for (int i = 0; i < input.length(); i++) {
                curString.append(input.charAt(i));
                if (i == input.length() - 1) {
                    outputFile.append(convertBinary(Integer.toBinaryString(dictionary.get(curString.toString())),
                            Integer.toBinaryString(maxSize - 1).length()));
                } else if (!dictionary.containsKey(curString.toString() + input.charAt(i + 1))) { // if currrent string
                                                                                                  // not in table add it
                                                                                                  // to table
                                                                                                  // with current index
                                                                                                  // = last index value
                                                                                                  // and clear cur
                                                                                                  // string and append
                                                                                                  // results to output
                                                                                                  // file
                    outputFile.append(convertBinary(Integer.toBinaryString(dictionary.get(curString.toString())),
                            Integer.toBinaryString(maxSize - 1).length()));
                    if (dictionary.size() < maxSize) {
                        dictionary.put(curString.append(input.charAt(i + 1)).toString(), dictionary.size());
                    }
                    curString.setLength(0);
                }
            }

            FileWriter fWriter = new FileWriter(inputPath);
            // write results

            fWriter.writeFie(outputFile.toString());
            String inputBinary = "";
            char[] inputChars = input.toCharArray();
            for (char c : inputChars) {
                inputBinary += convertBinary(Integer.toBinaryString(c), 8);
            }
            System.out.println("Input size: " + inputBinary.length() + "" + "\n");
            System.out.println("Output size: " + outputFile.length() + "" + "\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String convertBinary(String binary, int bit) {
        String convertedBinary = binary;
        // adjust string length to make it equal to max length
        for (int j = 0; j < (bit - binary.length()); j++) {
            convertedBinary = "0" + convertedBinary;
        }
        return convertedBinary;
    }

    public static void main(String[] args) {
        LZWEncoder encoder = new LZWEncoder();
        // max size will be converted to binary representation
        encoder.encode("lzw-file1", 512);
        // encoder.encode("lzw-file2", 512);
        // encoder.encode("lzw-file3", 512);
    }
}
