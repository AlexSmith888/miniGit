package infrastructure.filesystem;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class CopyDirectoryTree implements FileVisitor {
    Path source ;
    Path target;

    public CopyDirectoryTree(Path source, Path target) {
        this.source = source;
        this.target = target;
    }

    public String returnTail(Path source, Path trgt) {
        String[] arr = source.toString().split("/");
        String[] arr1 = trgt.toString().split("/");
        int i, j ;
        i = j = 0;

        while (i < arr1.length && j < arr.length && arr1[i].equals(arr[j])) {
            i++;
            j++;
        }
        var str = new StringBuilder();
        while (j < arr.length) {
            str.append(arr[j]);
            str.append("/");
            j++;
        }
        str.deleteCharAt(str.length() - 1);
        return str.toString();
    }

    @Override
    public FileVisitResult preVisitDirectory(Object dir, BasicFileAttributes attrs) throws IOException {
        Path current = (Path) dir;
        if (source.equals(dir)) {
            return CONTINUE;
        }
        System.out.println("Current : " + current);
        System.out.println("Copying to target : " + target);
        System.out.println("With the tail : " + returnTail(current, source));
        System.out.println("\n");

        String tail = returnTail(current, source);
        if (Files.exists(target.resolve(tail))) {
            return CONTINUE;
        }
        Files.createDirectory(target.resolve(tail));
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Object file, BasicFileAttributes attrs) throws IOException {
        Path current = (Path) file;

        System.out.println("Current : " + current);
        System.out.println("Copying to target : " + target);
        System.out.println("With the tail : " + returnTail(current, source));
        System.out.println("\n");

        String tail = returnTail(current, source);
        Files.copy(current, Path.of(target + "/" + tail), REPLACE_EXISTING);

        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Object file, IOException exc) throws IOException {
        if (exc != null) {
            System.out.println("Filed to copy the file : " + file);
            throw exc;
        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Object dir, IOException exc) throws IOException {
        if (exc != null) {
            System.out.println("Filed to copy directory : " + dir);
            throw exc;
        }
        return CONTINUE;
    }
}
