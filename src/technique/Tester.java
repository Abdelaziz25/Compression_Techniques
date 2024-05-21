package technique;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.DecimalFormat;

public class Tester {
    public static void testFile(String path, FileEncoding encoding) throws Exception {
        // Create output directory if it doesn't exist
        String outputPath = path.substring(0, path.lastIndexOf(File.separator) + 1) + "output";
        File outputDir = new File(outputPath);
        outputDir.mkdir();
        
        String originalHash = getMD5Checksum(path);
        System.out.println("Original MD5: " + originalHash);

        // Get original file size
        long length = new File(path).length();

        // Compress the file
        String compressedPath = outputPath + File.separator + new File(path).getName() + ".comp";
        long start = System.nanoTime();
        encoding.compress(path, compressedPath);
        long compressedLength = new File(compressedPath).length();
        long compressTime = System.nanoTime() - start;

        // Decompress the file
        String decompressedPath = outputPath + File.separator + new File(path).getName() + ".decomp";
        start = System.nanoTime();
        encoding.decompress(compressedPath, decompressedPath);
        long decompressTime = System.nanoTime() - start;

        // Get the hash of the decompressed file and compare it with the original hash
        String newHash = getMD5Checksum(decompressedPath);
        if(!originalHash.equals(newHash)){
            System.out.println("Incompatible hashes, aborting....");
        }

        // Get the compression ratio
        double ratio = (double) compressedLength / length;
        double spaceSaving = 1 - ratio;
        System.out.println(
                "Compressed MD5: " + newHash + "\n" +
                "Original size: " + formatFileSize(length) + "\n" +
                "Compressed size: " + formatFileSize(compressedLength) + "\n" +
                "Compression ratio: " + new DecimalFormat("#.##%").format(ratio) + "\n" +
                "Space saving: " + new DecimalFormat("#.##%").format(spaceSaving) + "\n" +
                "Compression time: " + compressTime / 1e6 + " ms\n" +
                "Decompression time: " + decompressTime / 1e6 + " ms"
        );
    }

    private static byte[] createChecksum(String filename) throws Exception {
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

    private static String getMD5Checksum(String filename) throws Exception {
        byte[] b = createChecksum(filename);
        StringBuilder result = new StringBuilder();

        for (byte value : b) {
            result.append(Integer.toString((value & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }

    private static String formatFileSize(long size) {
        if (size < 1024) {
            return size + " bytes";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else {
            return String.format("%.2f MB", size / (1024 * 1024.0));
        }
    }
}