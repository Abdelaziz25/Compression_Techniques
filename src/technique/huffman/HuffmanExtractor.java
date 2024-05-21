package technique.huffman;

import utils.ByteWithLength;
import utils.Bytes;

import java.io.*;
import java.util.HashMap;

public class HuffmanExtractor {
    String path;

    final int MAX_SIZE = 40*1024*1024;
    int inputLength;
    int inputPointer;
    int outputLength;
    byte[] inputBuffer;
    byte[] outputBuffer;

    BufferedInputStream inputStream;
    BufferedOutputStream outputStream;
    ByteWithLength input, output;
    long fileSize;
    int windowSize;

    public void extract(String path, String outputPath) throws IOException {
        this.path = path;
        init(outputPath);

        HashMap<String, Bytes> inverseTable = readTable(output, input, windowSize);

        writeFile(inverseTable);

        releaseResources();
    }

    private void init(String outputPath) throws IOException {
        this.inputLength = 0;
        this.inputPointer = 0;
        this.outputLength = 0;
        this.inputBuffer = new byte[MAX_SIZE];
        this.outputBuffer = new byte[MAX_SIZE];

        InputStream inputStream = new FileInputStream(path);
        this.inputStream = new BufferedInputStream(inputStream, MAX_SIZE);
        OutputStream outputStream = new FileOutputStream(outputPath);
        this.outputStream = new BufferedOutputStream(outputStream, MAX_SIZE);
        this.output = new ByteWithLength(0,0);
        this.input = new ByteWithLength(read(), 7);
        this.fileSize = readLong(output, input);
        this.windowSize = readInt(output, input);
    }

    private void releaseResources() throws IOException {
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    private void writeFile(HashMap<String, Bytes> inverseTable) throws IOException {
        byte buffer = input.len == 7 ? (byte) input.b : read();
        StringBuilder builder = new StringBuilder();
        int ignoredBytes = (int) (fileSize % windowSize);
        while (fileSize > ignoredBytes){
            for(int j = 7; j >= 0 && fileSize > ignoredBytes; j--){
                builder.append((buffer>>>j)&1);
                if(inverseTable.containsKey(builder.toString())){
                    write(inverseTable.get(builder.toString()).getBytes());
                    fileSize -= windowSize;
                    builder.setLength(0);
                }
            }
            buffer = read();
        }

        while(fileSize > 0){
            write(buffer);
            buffer = read();
            fileSize--;
        }

        if(outputLength > 0)
            outputStream.write(outputBuffer, 0, outputLength);
    }


    private HashMap<String, Bytes> readTable(ByteWithLength output, ByteWithLength input, int windowSize) throws IOException {
        long entries = readLong(output, input);
        HashMap<String, Bytes> table = new HashMap<>((int) entries);
        StringBuilder builder = new StringBuilder();
        while(entries > 0){
            byte[] key = new byte[windowSize];
            for(int i = 0; i < windowSize; i++){
                key[i] = readByte(output, input);
            }
            int bits = Byte.toUnsignedInt(readByte(output, input));
            while(bits > 0){
                builder.append((input.b>>>input.len)&1);
                bits--;
                input.len--;
                if (input.len < 0) {
                    input.b = read();
                    input.len = 7;
                }
            }
            table.put(builder.toString(), new Bytes(key));
            builder.setLength(0);
            entries--;
        }
        return table;
    }

    private long readLong(ByteWithLength output, ByteWithLength input) throws IOException {
        long result = 0;
        for (int i = 0; i < 8; i++) {
            result <<= 8;
            result |= (readByte(output, input) & 0xFF);
        }
        return result;
    }

    private int readInt(ByteWithLength output, ByteWithLength input) throws IOException {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result <<= 8;
            result |= (readByte(output, input) & 0xFF);
        }
        return result;
    }

    private byte readByte(ByteWithLength output, ByteWithLength input) throws IOException {
        while(output.len < 8) {
            output.b = (output.b << 1) | ((input.b >>> input.len) & 1);
            input.len--;
            output.len++;
            if (input.len < 0) {
                input.b = read();
                input.len = 7;
            }
        }
        byte b = (byte) output.b;
        output.b = 0;
        output.len = 0;
        return b;
    }

    private byte read() throws IOException {
        if(inputPointer == inputLength) {
            inputLength = inputStream.read(inputBuffer);
            inputPointer = 0;
        }
        return inputBuffer[inputPointer++];
    }

    private void write(byte[] array) throws IOException {
        for(byte b: array){
            outputBuffer[outputLength++] = b;
            if(outputLength == MAX_SIZE){
                outputStream.write(outputBuffer);
                outputLength = 0;
            }
        }
    }

    private void write(byte b) throws IOException{
        outputBuffer[outputLength++] = b;
        if(outputLength == MAX_SIZE){
            outputStream.write(outputBuffer);
            outputLength = 0;
        }
    }
}

