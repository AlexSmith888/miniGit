package infrastructure.filesystem;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.CONTINUE;

public class DiffDirectoryTree implements FileVisitor {
    String short1;
    String short2;
    Path root;

    public DiffDirectoryTree(Path path, String val1, String val2) {
        this.short1 = val1;
        this.short2 = val2;
        this.root = path;
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
        Path path = (Path) dir;
        if (dir.equals(root)) {
            return CONTINUE;
        }

        Path inverted = Path.of(path.toString().replace(short1, short2));

        if (!Files.exists(inverted)) {
            System.out.println("Directory : " + path
                    + " does not exist in " + short2);
        }

        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Object file, BasicFileAttributes attrs) throws IOException {
        Path file1 = (Path) file;
        Path file2 = Path.of(file1.toString().replace(short1, short2));

        if (!Files.exists(file2)) {
            System.out.format("%s does not exists in %s", file2, short2);
            System.out.println("\n");
            return CONTINUE;
        }

        if (!compareFiles(file1, file2)) {
            System.out.format("File %s in %s differs from %s in %s"
                    , file1, short1, file2, short2);
            System.out.println("\n");
        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Object file, IOException exc) throws IOException {
        return CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Object dir, IOException exc) throws IOException {
        return CONTINUE;
    }
}
