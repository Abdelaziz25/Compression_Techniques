import technique.FileEncoding;
import technique.arithmetic.ArithmeticEncoding;
import technique.huffman.HuffmanEncoding;
import technique.lzw.LZWEncoding;

import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final Map<String, FileEncoding> encodings = new HashMap<>() {{
        put("h", new HuffmanEncoding());
        put("a", new ArithmeticEncoding());
        put("l", new LZWEncoding());
    }};

    public static void main(String[] args) {
        if(args.length != 4) {
            System.out.println(
                    "Usage: encoding.jar algorithm c|d <input file name> <output file name>\n" +
                            "algorithm: h for Huffman, a for Arithmetic, l for LZW\n" +
                            "c for compress, d for decompress"
            );
            return;
        }

        String algorithm = args[0].substring(0, 1).toLowerCase();
        String mode = args[1].toLowerCase();
        String inputFileName = args[2];
        String outputFileName = args[3];

        FileEncoding encoding = encodings.get(algorithm);
        if(encoding == null) {
            System.out.println("Unsupported algorithm");
            return;
        }

        try {
            if(mode.equals("c"))
                encoding.compress(inputFileName, outputFileName);
            else if(mode.equals("d"))
                encoding.decompress(inputFileName, outputFileName);
            else
                System.out.println("Invalid mode");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
