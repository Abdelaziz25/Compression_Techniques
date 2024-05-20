package encoder;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class BinaryOut {

    private BufferedOutputStream out; // Buffered output stream for writing binary data
    private int buffer; // Buffer to store bits temporarily
    private int numOfRemainingBits; // Number of remaining bits in the buffer

    // Constructor to create BinaryOut object with specified file name
    public BinaryOut(String filename) {
        try {
            OutputStream os = new FileOutputStream(filename);
            out = new BufferedOutputStream(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Private method to write a single bit to the buffer
    private void writeBit(boolean x) {
        buffer <<= 1; // Shift buffer left by 1 bit
        if (x)
            buffer |= 1; // Set the least significant bit if x is true

        numOfRemainingBits++; // Increment the bit count
        if (numOfRemainingBits == 8)
            clearBuffer(); // If buffer is full, write it to the output stream
    }

    // Private method to flush the buffer to the output stream
    private void clearBuffer() {
        if (numOfRemainingBits == 0)
            return; // If buffer is empty, nothing to clear

        // Pad the buffer with zeros if necessary
        if (numOfRemainingBits > 0)
            buffer <<= (8 - numOfRemainingBits);

        try {
            out.write(buffer); // Write buffer to output stream
        } catch (IOException e) {
            e.printStackTrace();
        }

        numOfRemainingBits = 0; // Reset bit count
        buffer = 0; // Reset buffer
    }

    // Method to flush the buffer and output stream
    public void flush() {
        clearBuffer();
        try {
            out.flush(); // Flush the output stream
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to close the output stream
    public void close() {
        flush(); // Flush the buffer before closing
        try {
            out.close(); // Close the output stream
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to write a single bit to the output stream
    public void write(boolean x) {
        writeBit(x);
    }



}
