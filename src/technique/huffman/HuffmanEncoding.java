package technique.huffman;

import technique.FileCompressor;
import technique.Tester;

import java.io.IOException;

public class HuffmanEncoding implements FileCompressor {

    @Override
    public void compress(String inputFilePath, String compressedFilePath) throws IOException {
        HuffmanCompressor compressor = new HuffmanCompressor();
        compressor.compress(inputFilePath, compressedFilePath);
    }

    @Override
    public void decompress(String compressedFilePath, String outputFilePath) throws IOException {
        HuffmanExtractor extractor = new HuffmanExtractor();
        extractor.extract(compressedFilePath, outputFilePath);
    }

    public static void main(String[] args) throws Exception {
        Tester.testFile("test\\gbbct10.seq", new HuffmanEncoding());
    }
}
