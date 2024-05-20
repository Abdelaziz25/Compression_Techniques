package technique;

import technique.lzw.LZWEncoding;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.DecimalFormat;

public class Tester {
    public static void testFile(String path, FileCompressor compressor) throws Exception {
        String originalHash = getMD5Checksum(path);
        System.out.println("Original MD5: " + originalHash);

        // Get original file size
        long length = new File(path).length();
        
        // Compress the file
        String compressedPath = path + ".comp";
        long start = System.nanoTime();
        compressor.compress(path, compressedPath);
        long compressedLength = new File(compressedPath).length();
        long compressTime = System.nanoTime() - start;

        // Decompress the file
        String decompressedPath = path + ".decomp";
        start = System.nanoTime();
        compressor.decompress(compressedPath, decompressedPath);
        long decompressTime = System.nanoTime() - start;

        // Get the hash of the decompressed file and compare it with the original hash
        String newHash = getMD5Checksum(decompressedPath);
        if(!originalHash.equals(newHash)){
            System.out.println("Incompatible hashes, aborting....");
        }

        // Get the compression ratio
        double ratio = (double) compressedLength / length;
        System.out.println(
                "Compressed MD5: " + newHash + "\n" +
                "Original size: " + length + " bytes\n" +
                "Compressed size: " + compressedLength + " bytes\n" +
                "Compression ratio: " + new DecimalFormat("#.##").format(ratio) + "\n" +
                "Compression time: " + compressTime / 1e6 + " ms\n" +
                "Decompression time: " + decompressTime / 1e6 + " ms"
        );
    }

    public static byte[] createChecksum(String filename) throws Exception {
        InputStream fis =  new FileInputStream(filename);

        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;

        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);

        fis.close();
        return complete.digest();
    }

    public static String getMD5Checksum(String filename) throws Exception {
        byte[] b = createChecksum(filename);
        StringBuilder result = new StringBuilder();

        for (byte value : b) {
            result.append(Integer.toString((value & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }

    public static void main(String[] args) throws Exception {
        String path = "test/input1";
        testFile(path, new LZWEncoding());
        
    }
}