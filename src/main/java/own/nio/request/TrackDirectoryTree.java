package own.nio.request;

import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class TrackDirectoryTree implements FileVisitor {
    Path source;
    Path target;
    Path workingArea;
    public TrackDirectoryTree(Path src, Path trgt, Path workingArea){
        this.target = trgt;
        this.source = src;
        this.workingArea = workingArea;
    }

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

    @Override
    public FileVisitResult preVisitDirectory(Object dir, BasicFileAttributes attrs) throws IOException {
        Path current = (Path) dir;
        if (dir.equals(target)) {
            //System.out.println("Skipping : " + target.getParent());
            return FileVisitResult.SKIP_SUBTREE;
        }
        try {
            Path directory = workingArea.resolve(source.relativize(current));
            if (!Files.exists(directory)) {
                Files.createDirectory(directory);
            }
        } catch (IOException e) {
            return FileVisitResult.TERMINATE;
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Object file, BasicFileAttributes attrs) throws IOException {
        Path current = (Path) file;
        try {
            Path suggested = workingArea.resolve(source.relativize(current));
            if (Files.exists(suggested)) {
                if (!compareFiles(current, suggested)) {
                    //System.out.println(source + " " + suggested);
                    Files.copy(current, suggested, REPLACE_EXISTING);
                }
            }
            else {
                Files.copy(current, suggested, REPLACE_EXISTING);
            }
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
