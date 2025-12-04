package infrastructure.entities;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.DataTruncation;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class LocalFsTasksExecutor implements FileSystemGateway{
    @Override
    public void createDir(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
    }

    @Override
    public void removeDir(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            List<Path> ls = Files.list(path).toList();
            if (ls.isEmpty()) {
                Files.delete(path);
            }
        }
    }

    @Override
    public void copyDir(Path source, Path destination) throws IOException {
        Path target = Path.of(destination + "/" + source.getFileName());
        if (!Files.exists(target)) {
            Files.copy(source, target);
        }
    }

    @Override
    public void createFile(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
    }

    @Override
    public void removeFile(Path path) throws IOException {
        if (Files.exists(path) && Files.isRegularFile(path)) {
            Files.delete(path);
        }
    }

    @Override
    public void copyFile(Path source, Path destination) throws IOException {
        if (Files.isRegularFile(destination)) {
            destination = destination.getParent();
        }
        Path target = Path.of(destination + "/" + source.getFileName());
        if (Files.exists(target)) {
            Files.delete(target);
        }
        Files.createFile(target);
    }

    @Override
    public boolean isDirExists(Path source) throws IOException {
        return Files.exists(source) && Files.isDirectory(source);
    }

    @Override
    public boolean isFileExists(Path source) throws IOException {
        return Files.exists(source) && Files.isRegularFile(source);
    }

    @Override
    public void appendArowToTheFile(Path source, String row) throws IOException {
        if (isFileExists(source)) {
            Files.writeString(source, row, StandardOpenOption.APPEND);
            Files.writeString(source, "\n", StandardOpenOption.APPEND);
        }
    }
    @Override
    public List<String> readTheFile(Path path) throws IOException {
        return Files.readAllLines(path);
    }

    @Override
    public void copyRecursively(Path source, FileVisitor worker) throws IOException {
        Files.walkFileTree(source, worker);
    }

    @Override
    public void deleteRecursively(Path source, FileVisitor worker) throws IOException {
        Files.walkFileTree(source, worker);
    }
    @Override
    public void eraseRecursively(Path source, FileVisitor worker) throws IOException {
        Files.walkFileTree(source, worker);
    }

    @Override
    public void viewDifference(Path source, FileVisitor worker) throws IOException {
        Files.walkFileTree(source, worker);
    }
}
