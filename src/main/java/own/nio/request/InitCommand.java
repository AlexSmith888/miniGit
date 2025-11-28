package own.nio.request;

import own.nio.core.Command;
import own.nio.core.MIniGitClass;
import own.nio.utils.CachedDirectories;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class InitCommand implements Command {
    @Override
    public void execute(MIniGitClass entity) throws IOException {

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
            IO.println("Impossible to create .vcs folder");
            System.out.println(e.getMessage());
            throw new IOException("impossible to complete init command");
        }
    }
}
