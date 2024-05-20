import java.io.*;
import java.nio.file.*;
import java.util.*;

public class LZWDecoder {
    static boolean debugging = false;

    // read file
    public String readFile(String inputFile) {
        try {
            // Read all bytes from the input file
            byte[] bits = Files.readAllBytes(Paths.get(inputFile));

            // If the file is empty, write a message to the output file and return
            if (bits.length == 0) {
                System.out.println("The file is empty.");
                return "";
            }

            // Convert the bytes to binary representation
            StringBuilder binary = new StringBuilder();
            for (byte bit : bits) {
                binary.append(toBinary(bit));
            }
            return binary.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    ArrayList<Integer> divide(String binary, int step) {
        ArrayList<Integer> values = new ArrayList<>();

        for (int i = 0; i <= binary.length() - step; i += step) {
            values.add(binaryValue(binary.substring(i, i + step), step));
        }
        return values;
    }

    public void createTable(HashMap<Integer, String> dict) {
        for (int i = 0; i < 256; i++) {
            dict.put(i, "" + (char) i);
        }
    }

    public void decode(ArrayList<Integer> values, String outputFile) {

        if (debugging) {
            // Print the list of binary values (for debugging)
            System.out.println(values);
        }
        // Initialize a dictionary with ASCII characters
        HashMap<Integer, String> dict = new HashMap<>();
        // create Ascii table
        createTable(dict);

        // Initialize variables for encoding
        int nextVal = 256;
        int old = values.get(0);
        String s = dict.get(old);
        String c = "" + s.charAt(0);
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)))) {
            // Write the initial string to the output file
            out.print(s);
            // Iterate through the binary values to decode
            for (int i = 1; i < values.size(); i++) {
                int next = values.get(i);
                // Check if the value is in the dictionary
                if (!dict.containsKey(next)) {
                    // If not, reconstruct the string
                    s = dict.get(old);
                    if (debugging) {
                        System.out.println(s.charAt(0));
                    }
                    s = s + c;
                } else {
                    // If yes, retrieve the string from the dictionary
                    s = dict.get(next);
                }
                // Write the decoded string to the output file
                out.print(s);
                // Update the character c and add the new string to the dictionary
                c = "" + s.charAt(0);
                dict.put(nextVal, dict.get(old) + c);
                System.out.println(dict.get(nextVal));

                nextVal++;
                old = next;
            }
            // End of file
            out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Convert binary string to integer value
    public int binaryValue(String a, int step) {
        int ans = 0;
        for (int i = 0; i < step; i++) {
            if (a.charAt(i) == '1') {
                ans += (1 << (8 - i));
            }
        }
        return ans;
    }

    // Convert integer to 8-bit binary string
    public String toBinary(int a) {
        String cur = Integer.toBinaryString(a);
        StringBuilder ans = new StringBuilder();
        while (cur.length() + ans.length() < 8) {
            ans.append("0");
        }
        if (cur.length() > 8) {
            cur = cur.substring(cur.length() - 8);
        }
        ans.append(cur);
        return ans.toString();
    }

    public void lzwDecode(String inputFile, String outputFile, int maxSize) {
        // convert maxSize to binary length
        int step = Integer.toBinaryString(maxSize - 1).length();
        // Read the binary string from the input file
        String binary = readFile(inputFile);
        // Divide the binary string into 12-bit values
        ArrayList<Integer> values = divide(binary, step);
        // Decode the values and write the output to the output file
        decode(values, outputFile);
    }

    public static void main(String[] args) throws IOException {
        // Call decode method with input and output file paths, and maximum size
        LZWDecoder decoder = new LZWDecoder();
        decoder.lzwDecode("LZW-Compression-master\\lzw-file2.comp", "lzw-decoded2.txt", 512);
        // decoder.decode("lzw-file3.comp", "lzw-decoded3.txt", 512);

    }
}
