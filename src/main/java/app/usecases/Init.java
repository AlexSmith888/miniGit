package app.usecases;

import domain.services.Request;
import domain.entities.MIniGitRepository;
import infrastructure.filesystem.MoveDirectoryTree;
import infrastructure.cache.CachedDirectories;

import java.io.IOException;
import java.nio.file.Files;

public class Init implements Request {
    @Override
    public void execute(MIniGitRepository entity) throws IOException {

        try {
            Files.createDirectory(entity.returnSourceGitDir());
            Files.createDirectory(entity.returnSourceGitTempDir());
            Files.createDirectory(entity.returnSourceGitCommitDir());
            if (!CachedDirectories.returnDirectories().contains(entity.returnSourceDir())) {
                CachedDirectories.returnDirectories().add(entity.returnSourceDir());
            }
            Files.walkFileTree(entity.returnSourceDir()
                    , new MoveDirectoryTree(entity.returnSourceDir(), entity.returnSourceGitTempDir()));
        } catch (IOException e) {
            System.out.println("Impossible to create .vcs folder");
            System.out.println(e.getMessage());
            throw new IOException("impossible to complete init command");
        }
    }
}
