package app.usecases;

import domain.services.Request;
import domain.entities.MIniGitRepository;
import infrastructure.filesystem.UndoDirectoryTree;
import infrastructure.cache.CachedCommitTrees;
import infrastructure.cache.CachedDirectories;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Undo implements Request {
    @Override
    public void execute(MIniGitRepository entity) throws IOException {

        Path source = entity.returnSourceDir();
        Path vcsFolder = entity.returnSourceGitDir();

        try {
            Files.walkFileTree(vcsFolder, new UndoDirectoryTree());
            CachedDirectories.removeDirectory(source);
            CachedCommitTrees.removeTree(source);
        } catch (IOException e) {
            System.out.println("Impossible to delete miniGit folder");
            System.out.println(e.getMessage());
            throw new IOException("impossible to complete undo command");
        }
    }
}
