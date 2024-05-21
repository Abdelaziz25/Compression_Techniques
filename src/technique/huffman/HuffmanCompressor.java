package technique.huffman;

import utils.ByteWithLength;
import utils.Bytes;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HuffmanCompressor {
    Map<Bytes, String> table;
    final int MAX_SIZE = 40*1024*1024;
    int inputOffset = 0;
    int inputLength = 0;
    int outputLength = 0;
    byte[] inputBuffer = new byte[MAX_SIZE];
    byte[] outputBuffer = new byte[MAX_SIZE];
    InputStream inputStream;
    BufferedInputStream bufferedInputStream;
    BufferedOutputStream outputStream;
    long originalFileLength;
    int windowSize;

    private void init(String path, int windowSize) throws FileNotFoundException {
        this.inputOffset = 0;
        this.inputLength = 0;
        this.outputLength = 0;
        this.inputBuffer = new byte[MAX_SIZE];
        this.outputBuffer = new byte[MAX_SIZE];

        File file = new File(path);
        this.originalFileLength = file.length();
        this.inputStream = new FileInputStream(file);
        this.bufferedInputStream = new BufferedInputStream(this.inputStream, MAX_SIZE);

        this.windowSize = windowSize;
        if(windowSize > originalFileLength && originalFileLength != 0) {
            System.out.println("Warning! n is bigger than file size, setting n = " + this.originalFileLength);
            this.windowSize = (int) this.originalFileLength;
        }
    }

    private void initWriting(String path, String outputPath) throws IOException {
        File file = new File(path);
        inputStream.close();
        inputStream = new FileInputStream(file);
        bufferedInputStream = new BufferedInputStream(inputStream, MAX_SIZE);
        OutputStream outputStream = new FileOutputStream(outputPath);
        this.outputStream = new BufferedOutputStream(outputStream, MAX_SIZE);
    }

    private void releaseResources() throws IOException {
        outputStream.flush();
        outputStream.close();
        bufferedInputStream.close();
        inputStream.close();
    }

    public void compress(String path, String outputPath) throws IOException {
        int windowSize = 1;
        init(path, windowSize);

        this.table = getHuffmanCode();

        initWriting(path, outputPath);
        long remaining = originalFileLength;
        ByteWithLength output = new ByteWithLength( 0, 0);
        writeLong(remaining, output);
        writeInt(windowSize, output);
        writeTable(output, table);
        outputStream.flush();

        writeData(output);
        releaseResources();
    }

    private void writeData(ByteWithLength output) throws IOException {
        long remaining = originalFileLength;
        inputOffset = 0;
        inputLength = 0;
        while(remaining > 0){
            inputLength = bufferedInputStream.read(inputBuffer, inputOffset, MAX_SIZE-inputOffset);
            int actualLength = inputLength + inputOffset;
            remaining -= inputLength;
            writeEncoded(inputBuffer, actualLength, windowSize, output, remaining);
            inputOffset = actualLength % windowSize;
            if(inputOffset != 0)
                System.arraycopy(inputBuffer, actualLength - inputOffset, inputBuffer, 0, inputOffset);

        }
        if (inputOffset > 0){
            for(int i = 0; i < inputOffset; i++)
                write(inputBuffer[i]);
        }
        if(outputLength > 0)
            outputStream.write(outputBuffer, 0, outputLength);
    }

    private HashMap<Bytes, String> getHuffmanCode() throws IOException {
        Encoder encoder = new Encoder();
        long remaining = originalFileLength;
        int ignoredBytes = (int) (remaining % windowSize);
        while(remaining > ignoredBytes){
            inputLength = inputStream.read(inputBuffer, inputOffset, MAX_SIZE-inputOffset);
            int actualLength = inputLength + inputOffset;
            remaining -= inputLength;
            encoder.calculateFrequencies(inputBuffer, actualLength, windowSize);
            inputOffset = actualLength % windowSize;
            if(inputOffset != 0){
                System.arraycopy(inputBuffer, actualLength - inputOffset, inputBuffer, 0, inputOffset);
            }
        }
        Node root = encoder.constructTree();
        return encoder.getCodes(root);
    }

    // First writes # of entries in long (8 bytes) -> up to 9,223,372,036,854,775,807 entry
    // Then writes bytes corresponding to each code (windowSize bytes) -> variable (depends on windowSize)
    // Then for each record, it writes # of bits in code in byte -> up to 255 bit
    // Then writes code bits (variable size)
    private void writeTable(ByteWithLength output, Map<Bytes, String> table) throws IOException {
        writeLong((long) table.size(), output);
        for(Map.Entry<Bytes, String> entry: table.entrySet()){
            String code = entry.getValue();
            byte[] key = entry.getKey().getBytes();
            for(byte k: key)        // Write each byte for the key
                writeByte(k, output);
            byte len = (byte) code.length();        // Write code length
            writeByte(len, output);
            writeCode(code, output);  // Write code bits
        }
        if(output.len > 0){
            output.b <<= 8-output.len;
            write((byte) output.b);
            output.b = 0;
            output.len = 0;
        }
    }

    private void writeByte(byte b ,ByteWithLength output) throws IOException {
        for(int j = 7; j >= 0; j--){
            output.b = (output.b << 1) | ((b >>> j)&(1));
            output.len++;
            writeIfFullByte(output);
        }
    }

    private void writeLong(Long l, ByteWithLength output) throws IOException {
        for(int i = 7; i >= 0; i--){
            writeByte((byte) ((l >>> (8*i)) & (0b11111111)), output);
        }
    }

    private void writeInt(int l, ByteWithLength output) throws IOException {
        for(int i = 3; i >= 0; i--){
            writeByte((byte) ((l >>> (8*i)) & (0b11111111)), output);
        }
    }

    private void writeCode(String code, ByteWithLength output) throws IOException {
        for(int j = 0; j < code.length(); j++){
            output.b = (output.b << 1) | Character.getNumericValue(code.charAt(j));
            output.len++;
            writeIfFullByte(output);
        }
    }

    private void writeIfFullByte(ByteWithLength output) throws IOException {
        if(output.len >= 8){
            write((byte) output.b);
            output.b = 0;
            output.len = 0;
        }
    }

    private void write(byte b) throws IOException {
        outputBuffer[outputLength++] = b;
        if(outputLength >= MAX_SIZE){
            outputStream.write(outputBuffer);
            outputLength = 0;
        }
    }

    private ByteWithLength writeEncoded(byte[] inputBuffer, int bufferLength, int windowSize, ByteWithLength output, long remaining) throws IOException {
        assert output.len < 8;
        for(int i = 0; i < bufferLength/windowSize; i++) {
            String code = table.get(new Bytes(Arrays.copyOfRange(inputBuffer, i*windowSize, (i+1)*windowSize)));
            writeCode(code, output);
        }
        if (output.len > 0 && remaining <= 0){
            output.b <<= 8 - output.len;
            write((byte) output.b);
        }
        return output;
    }
}
