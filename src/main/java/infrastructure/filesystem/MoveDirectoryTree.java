package infrastructure.filesystem;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class MoveDirectoryTree implements FileVisitor {
    Path source;
    Path target;

    public MoveDirectoryTree (Path src, Path trgt){
        this.source = src;
        this.target = trgt;
    }
    @Override
    public FileVisitResult preVisitDirectory(Object dir, BasicFileAttributes attrs) throws IOException {
        Path current = (Path) dir;
        if (dir.equals(target.getParent())) {
            return FileVisitResult.SKIP_SUBTREE;
        }
        try {
            Files.createDirectories(target.resolve(source.relativize(current)));
        } catch (IOException e) {
            return FileVisitResult.TERMINATE;
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Object file, BasicFileAttributes attrs) throws IOException {
        Path current = (Path) file;
        try {
            Files.copy(current, target.resolve(source.relativize(current)), REPLACE_EXISTING);
        } catch (IOException e) {
            return FileVisitResult.TERMINATE;
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Object file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Object dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }
}
