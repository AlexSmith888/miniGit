package own.nio.request;

import own.nio.core.Command;
import own.nio.core.MIniGitClass;
import own.nio.utils.CachedCommitTrees;
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
    public void execute(MIniGitClass entity) throws IOException {

        Path source = entity.returnSourceDir();
        Path vcsFolder = entity.returnSourceGitDir();

        try {
            Files.walkFileTree(vcsFolder, new UndoDirectoryTree());
            CachedDirectories.removeDirectory(source);
            CachedCommitTrees.removeTree(source);
        } catch (IOException e) {
            IO.println("Impossible to delete miniGit folder");
            System.out.println(e.getMessage());
            throw new IOException("impossible to complete undo command");
        }
    }
}
