package own.nio.request;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.CONTINUE;

public class PurgeTempDirectoryTree implements FileVisitor {
    Path toBeignored;
    PurgeTempDirectoryTree (Path miniGit){
        toBeignored = miniGit;
    }
    @Override
    public FileVisitResult preVisitDirectory(Object dir, BasicFileAttributes attrs) throws IOException {
        Path root = (Path) dir;
        if (root.equals(toBeignored)) {
            System.out.println(dir.toString() + "will be ignored");
            return CONTINUE;
        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Object file, BasicFileAttributes attrs) throws IOException {
        Path current = (Path) file;
        if (current.equals(toBeignored)) {
            return CONTINUE;
        }
        System.out.println("Deleting a file : " + file.toString());
        Files.delete(current);
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Object file, IOException exc) throws IOException {
        if (exc != null) {
            System.out.println("Error while removing a file : "
                    + " " + file.toString());
            throw exc;
        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Object dir, IOException exc) throws IOException {
        Path root = (Path) dir;
        if (root.equals(toBeignored)) {
            return CONTINUE;
        }
        System.out.println("Deleting a file : " + dir);
        Files.delete(root);
        return CONTINUE;
    }
}
