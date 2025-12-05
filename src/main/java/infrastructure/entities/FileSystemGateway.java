package infrastructure.entities;

import infrastructure.storage.JsonData;

import java.io.IOException;
import java.nio.file.FileVisitor;
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
    public void copyRecursively(Path source, FileVisitor worker) throws IOException;
    public void deleteRecursively(Path source, FileVisitor worker) throws IOException;
    public void eraseRecursively(Path source, FileVisitor worker) throws IOException;
    public void viewDifference(Path source, FileVisitor worker) throws IOException;
    public void writeJsonToTheDisk(Path source, JsonData file) throws IOException;

    public Path returnfullPath(Path path, String fileName) throws IOException;
    public void printJson(Path path) throws IOException;
    public void copyDirWithSuffix(Path source, Path destination) throws IOException;
    public List<Path> listOfObjectsInFolder (Path source) throws IOException;
}
