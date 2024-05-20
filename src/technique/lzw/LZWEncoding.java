package technique.lzw;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;

import utils.BytesUtils;
import utils.Bytes;
import technique.FileCompressor;

public class LZWEncoding extends FileCompressor{

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
    public void compress(String inputFilePath, String compressedFilePath) throws FileNotFoundException {
        DataInputStream fileReader = new DataInputStream(new FileInputStream(inputFilePath));
        DataOutputStream fileWriter = new DataOutputStream(new FileOutputStream(compressedFilePath));
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
                    table.put(next, nextVal++);
                    current = new Bytes(new byte[] { (byte) b });
                } else {
                    current = next;
                }
            }
            BytesUtils.writeCompressedInt(table.get(current), fileWriter);

            fileReader.close();
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void decompress(String compressedFilePath, String outputFilePath) throws FileNotFoundException{
        DataInputStream fileReader = new DataInputStream(new FileInputStream(compressedFilePath));
        DataOutputStream fileWriter = new DataOutputStream(new FileOutputStream(outputFilePath));

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
                Bytes newEntry = new Bytes(prevEntry.getBytes());
                newEntry.append(currentEntry.get(0));

                table.put(nextVal++, newEntry);
                prevEntry = currentEntry;
            }

            fileReader.close();
            fileWriter.flush();
            fileWriter.close();
        } catch (EOFException e) {
            // Do nothing
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
