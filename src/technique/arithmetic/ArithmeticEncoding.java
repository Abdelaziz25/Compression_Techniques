package technique.arithmetic;

import technique.FileEncoding;
import technique.Tester;

import java.io.IOException;

public class ArithmeticEncoding implements FileEncoding {
    private final ArithmeticEncoder encoder;
    private final ArithmeticDecoder decoder;

    public ArithmeticEncoding(){
        encoder = new ArithmeticEncoder();
        decoder = new ArithmeticDecoder();
    }

    @Override
    public void compress(String inputFilePath, String compressedFilePath) throws IOException {
        encoder.encode(inputFilePath, compressedFilePath);
    }

    @Override
    public void decompress(String compressedFilePath, String outputFilePath) throws IOException {
        decoder.decode(compressedFilePath, outputFilePath);
    }

    public static void main(String[] args) throws Exception {
        Tester.testFile("test\\random.txt", new ArithmeticEncoding());
    }
}
