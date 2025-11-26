package own.nio.request;

import own.nio.core.Command;
import own.nio.utils.CachedDirectories;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class UndoCommand implements Command {
    @Override
    public void execute(Object[] items) throws IOException {
        String[] arr = (String[]) items;

        Path source = Path.of(arr[1]);
        Path vcsFolder = source.resolve("miniGit");

        try {
            Files.walkFileTree(vcsFolder, new UndoDirectoryTree());
            CachedDirectories.removeDirectory(source);
        } catch (IOException e) {
            IO.println("Impossible to delete miniGit folder");
            System.out.println(e.getMessage());
            throw new IOException("impossible to complete undo command");
        }
    }
}
