package app.usecases;

import domain.services.Request;
import domain.entities.MIniGitRepository;
import infrastructure.filesystem.MoveDirectoryTree;

import java.io.IOException;
import java.nio.file.Files;

public class Init implements Request {
    @Override
    public void execute(MIniGitRepository entity) throws IOException {

        try {
            entity.returnFileSystem().createDir(entity.returnSourceGitDir());
            entity.returnFileSystem().createDir(entity.returnSourceGitTempDir());
            entity.returnFileSystem().createDir(entity.returnSourceGitCommitDir());

            if (!entity.returnRepos().returnCachedDirectories()
                    .contains(entity.returnSourceDir())) {
                entity.returnRepos().returnCachedDirectories()
                        .add(entity.returnSourceDir());
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
