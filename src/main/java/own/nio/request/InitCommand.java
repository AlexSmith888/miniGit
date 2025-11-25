package own.nio.request;

import own.nio.core.Command;
import own.nio.utils.CachedDirectories;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class InitCommand implements Command {
    @Override
    public void execute(Object[] items) throws IOException {
        String[] arr = (String[]) items;

        Path source = Path.of(arr[1]);
        Path vcsFolder = source.resolve("miniGit");
        Path workingArea = vcsFolder.resolve("temp");
        Path commitsTree = vcsFolder.resolve("commits");

        try {
            Files.createDirectory(vcsFolder);
            Files.createDirectory(workingArea);
            Files.createDirectory(commitsTree);
            if (!CachedDirectories.returnDirectories().contains(source)) {
                CachedDirectories.returnDirectories().add(source);
            }
            Files.walkFileTree(source, new MoveDirectoryTree(source, workingArea));
        } catch (IOException e) {
            IO.println("Impossible to create .vcs folder");
            System.out.println(e.getMessage());
            throw new IOException("impossible to complete init command");
        }
    }
}
