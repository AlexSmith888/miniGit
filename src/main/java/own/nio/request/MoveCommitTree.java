package own.nio.request;

import own.nio.core.Commands;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class MoveCommitTree implements FileVisitor {
    String from;
    String to;
    Path src;

    public MoveCommitTree (Path wrk, String src, String trgt){
        this.from = src;
        this.to = trgt;
        this.src = wrk;
    }
    @Override
    public FileVisitResult preVisitDirectory(Object dir, BasicFileAttributes attrs) throws IOException {
        Path current = (Path) dir;
        if (src.equals(current)) {
            return CONTINUE;
        }
        Path dest = Path.of(current.toString().replace(from, to));
        if (!Files.exists(dest)) {
            Files.createDirectory(dest);
        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Object file, BasicFileAttributes attrs) throws IOException {
        Path current = (Path) file;
        Path dest = Path.of(current.toString().replace(from, to));
        if (!Files.exists(dest)) {
            Files.copy(current, dest, REPLACE_EXISTING);
        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Object file, IOException exc) throws IOException {
        if (exc != null) {
            throw exc;
        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Object dir, IOException exc) throws IOException {
        if (exc != null) {
            throw exc;
        }
        return CONTINUE;
    }
}
