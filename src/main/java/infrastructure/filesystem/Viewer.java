package infrastructure.filesystem;

import infrastructure.entities.RecursiveWorker;
import infrastructure.filescomparison.FilesComparator;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.CONTINUE;

public class Viewer implements FileVisitor, RecursiveWorker {
    private Path source;
    private Path target;
    private FilesComparator comparator;
    public Viewer(FilesComparator comparator){
        this.comparator = comparator;
    }

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

    }

    @Override
    public void truncateExcludedList() {

    }

    @Override
    public FileVisitResult preVisitDirectory(Object dir, BasicFileAttributes attrs) throws IOException {
        Path current = (Path) dir;
        if (dir.equals(source)) {
            return CONTINUE;
        }

        Path relative = target.resolve(source.relativize(current));

        if (!Files.exists(relative)) {
            System.out.format("Directory %s: DOES NOT EXIST in %s"
                    , dir, target);
            System.out.println("\n");
        }

        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Object file, BasicFileAttributes attrs) throws IOException {
        Path current = (Path) file;

        Path relative = target.resolve(source.relativize(current));

        if (!Files.exists(relative)) {
            System.out.format("File %s: DOES NOT EXISTS in %s"
                    , current.getFileName(), target);
            System.out.println("\n");
            return CONTINUE;
        }
        if (!comparator.compareFiles(current, relative)) {
            System.out.format("File %s in %s: DIFFERS from the file in %s"
                    , source.getFileName(), source, target);
            System.out.println("\n");
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
