package infrastructure.filescomparison;

import java.io.IOException;
import java.nio.file.Path;

public interface FilesComparator {
    boolean compareFiles(Path file1, Path file2) throws IOException;
}
