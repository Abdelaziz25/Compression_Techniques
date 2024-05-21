package technique;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Interface for file compressors and decompressors.
 */
public interface FileEncoding {

    /**
     * Compresses a file.
     * @param inputFilePath The path to the file to compress.
     * @param compressedFilePath The path to the result compressed file.
     * @throws IOException If an I/O error occurs.
     * @throws FileNotFoundException If the file is not found.
     */
    void compress(String inputFilePath, String compressedFilePath) throws IOException, FileNotFoundException;

    /**
     * Decompresses a file.
     * @param compressedFilePath The path to the compressed file.
     * @param outputFilePath The path to the result decompressed file.
     * @throws IOException If an I/O error occurs.
     * @throws FileNotFoundException If the file is not found.
     */
    void decompress(String compressedFilePath, String outputFilePath) throws IOException, FileNotFoundException;
}