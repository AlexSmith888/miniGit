package infrastructure.entities;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface FileSystemGateway {
    public void createDir(Path path) throws IOException;
    public void removeDir(Path path) throws IOException;
    public void copyDir(Path source, Path destination) throws IOException;
    public void createFile(Path path) throws IOException;
    public void removeFile(Path path) throws IOException;
    public void copyFile(Path source, Path destination) throws IOException;
    public boolean isDirExists(Path source) throws IOException;
    public boolean isFileExists(Path source) throws IOException;
    public void appendArowToTheFile(Path source, String row) throws IOException;
    public List<String> readTheFile(Path source) throws IOException;
}
