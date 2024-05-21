package technique.lzw;

import java.io.*;
import java.util.HashMap;

import technique.FileEncoding;
import technique.Tester;
import utils.BytesUtils;
import utils.Bytes;

public class LZWEncoding implements FileEncoding {
    private static final int BUFFER_SIZE = 4 * 1024;

    private final int maxTableIndex;

    public LZWEncoding(){
        maxTableIndex = 16 * 1024 * 1024;
    }

    public LZWEncoding(int maxTableIndex){
        this.maxTableIndex = maxTableIndex;
    }

    private HashMap<Bytes, Integer> initializeCompressionTable() {
        HashMap<Bytes, Integer> table = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            table.put(new Bytes(new byte[] { (byte) i }), i);
        }
        return table;
    }

    private HashMap<Integer, Bytes> initializeDecompressionTable() {
        HashMap<Integer, Bytes> table = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            table.put(i, new Bytes(new byte[] { (byte) i }));
        }
        return table;
    }

    @Override
    public void compress(String inputFilePath, String compressedFilePath) throws IOException {
        DataInputStream fileReader = new DataInputStream(
                new BufferedInputStream(new FileInputStream(inputFilePath), BUFFER_SIZE));
        DataOutputStream fileWriter = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(compressedFilePath), BUFFER_SIZE));
        HashMap<Bytes, Integer> table = initializeCompressionTable();

        int nextVal = 256;
        Bytes current = new Bytes(new byte[] {});
        try {
            int b;
            while ((b = fileReader.read()) != -1) {
                Bytes next = new Bytes(current.getBytes());
                next.append((byte) b);
                if (!table.containsKey(next)) {
                    BytesUtils.writeCompressedInt(table.get(current), fileWriter);
                    if(nextVal < maxTableIndex)
                        table.put(next, nextVal++);
                    current = new Bytes(new byte[] { (byte) b });
                } else {
                    current = next;
                }
            }
            BytesUtils.writeCompressedInt(table.get(current), fileWriter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        fileReader.close();
        fileWriter.close();
    }

    @Override
    public void decompress(String compressedFilePath, String outputFilePath) throws IOException {
        DataInputStream fileReader = new DataInputStream(
                new BufferedInputStream(new FileInputStream(compressedFilePath), BUFFER_SIZE));
        BufferedOutputStream fileWriter = new BufferedOutputStream(new FileOutputStream(outputFilePath), BUFFER_SIZE);

        HashMap<Integer, Bytes> table = initializeDecompressionTable();
        int nextVal = 256;
        try {
            int current = BytesUtils.readCompressedInt(fileReader);
            Bytes prevEntry = table.get(current);
            fileWriter.write(prevEntry.getBytes());
            int nextEncoded;
            while ((nextEncoded = BytesUtils.readCompressedInt(fileReader)) != -1) {
                Bytes currentEntry;
                if (table.containsKey(nextEncoded)) {
                    currentEntry = table.get(nextEncoded);
                } else if (nextEncoded == nextVal) {
                    currentEntry = new Bytes(prevEntry.getBytes());
                    currentEntry.append(prevEntry.get(0));
                } else {
                    throw new Exception("Invalid input file");
                }
                fileWriter.write(currentEntry.getBytes());
                if(nextVal < maxTableIndex) {
                    Bytes newEntry = new Bytes(prevEntry.getBytes());
                    newEntry.append(currentEntry.get(0));
                    table.put(nextVal++, newEntry);
                }
                prevEntry = currentEntry;
            }
        } catch (EOFException e) {
            // Do nothing
        } catch (Exception e) {
            e.printStackTrace();
        }
        fileReader.close();
        fileWriter.close();
    }

    public static void main(String[] args) throws Exception {
        Tester.testFile("test\\lecture.pdf", new LZWEncoding());
    }
}
