package infrastructure.filescomparison;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

public class ByteToByteComparator implements FilesComparator{
    @Override
    public boolean compareFiles(Path file1, Path file2) throws IOException {
        FileInputStream read1 = new FileInputStream(file1.toFile());
        FileInputStream read2 = new FileInputStream(file2.toFile());
        BufferedInputStream buf = new BufferedInputStream(read1);
        BufferedInputStream buf2 = new BufferedInputStream(read2);

        int value, value1;
        while (true) {
            value = buf.read();
            value1 = buf2.read();
            if (value != value1) {
                return false;
            }
            if (value == -1) {
                return true;
            }
        }
    }
}
