package utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

public class BytesUtils {
    public static int readCompressedInt(DataInputStream in) throws IOException, EOFException {
        int nBytes = in.readByte();
        int value = 0;
        for (int i = 0; i < nBytes; i++) {
            value = (value << 8) | (in.readByte() & 0xFF);
        }
        return value;
    }

    public static void writeCompressedInt(int value, DataOutputStream out) throws IOException {
        int nBytes = 0;
        for (int i = 0; i < 4; i++) {
            if ((value >> (8 * i)) != 0) {
                nBytes = i + 1;
            }
        }
        out.writeByte(nBytes);
        for (int i = nBytes - 1; i >= 0; i--) {
            out.writeByte((value >> (8 * i)) & 0xFF);
        }
    }
}
