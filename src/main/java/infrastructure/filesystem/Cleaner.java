package infrastructure.filesystem;

import infrastructure.entities.RecursiveWorker;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;

public class Cleaner implements RecursiveWorker, FileVisitor {
    private Path source;
    private Path target;
    private final List<Path> excluded = new ArrayList<>();

    @Override
    public void setSource(Path source) {
        this.source = source;
    }

    @Override
    public void setTarget(Path target) {
        this.target = target;
    }

    @Override
    public void addToExcludedList(Path path) {
        excluded.add(path);
    }

    @Override
    public FileVisitResult preVisitDirectory(Object dir, BasicFileAttributes attrs) throws IOException {
        Path current = (Path) dir;
        if (excluded.contains(current)) {
            return SKIP_SUBTREE;
        }
        if (dir.equals(source)) {
            return CONTINUE;
        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Object file, BasicFileAttributes attrs) throws IOException {
        Path current = (Path) file;
        Files.delete(current);
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Object file, IOException exc) throws IOException {
        if (exc != null) {
            System.out.println("The error occurred while deleting the file : " + ((Path) file).toString());
            throw exc;
        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Object dir, IOException exc) throws IOException {
        Path current = (Path) dir;
        if (exc != null) {
            System.out.println("The error occurred while deleting the directory : " + current);
            throw exc;
        }
        if (dir.equals(source)) {
            return CONTINUE;
        }
        Files.delete(current);
        return CONTINUE;
    }
}
