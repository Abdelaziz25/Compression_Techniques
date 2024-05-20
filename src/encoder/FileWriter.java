package encoder;

public class FileWriter {
    String fileName;

    FileWriter(String fileName) {
        this.fileName = fileName;
    }

    void writeFie(String output) {
        try {
            // Write output
            BinaryOut out = new BinaryOut(fileName + ".comp");
            for (int i = 0; i < output.length(); i++) {
                out.write(output.charAt(i) == '1');
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
