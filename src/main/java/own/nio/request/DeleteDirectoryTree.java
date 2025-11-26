package own.nio.request;

import javax.xml.transform.Source;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.CONTINUE;

public class DeleteDirectoryTree implements FileVisitor {

    Path source;
    Path target;

    public DeleteDirectoryTree (Path src, Path dest){
        this.source = src;
        this.target = dest;
    }

    @Override
    public FileVisitResult preVisitDirectory(Object dir, BasicFileAttributes attrs) throws IOException {
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Object file, BasicFileAttributes attrs) throws IOException {
        Path filepath = (Path) file;
        Path  sourceFile = source.resolve(source.relativize(filepath));

        sourceFile = Path.of(sourceFile.toString()
                .replace("/temp", "")
                .replace("miniGit", ""));

        if (!Files.exists(sourceFile)) {
            Files.delete(filepath);
            /*System.out.format("Removing the file %s from working directory %s"
                    ,filepath.toFile(), filepath.getParent());*/
        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Object file, IOException exc) throws IOException {
        if (exc != null) {
            System.out.println("A failure happened while deleting the file : " + file.toString());
        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Object dir, IOException exc) throws IOException {
        Path filepath = (Path) dir;
        Path  sourceFile = source.resolve(source.relativize(filepath));
        if (!Files.exists(sourceFile)) {
            /*System.out.format("Removing the directory %s from working directory tree"
                    ,filepath.toFile());
            IO.println("\n");*/
            Files.delete(filepath);
        }
        return CONTINUE;
    }
}
