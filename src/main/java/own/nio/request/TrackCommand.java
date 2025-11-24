package own.nio.request;

import own.nio.core.Command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TrackCommand implements Command {
    @Override
    public void execute(Object[] items) throws IOException {
        String[] arr = (String[]) items;

        Path source = Path.of(arr[1]);
        Path vcsFolder = source.resolve("miniGit");
        Path workingArea = vcsFolder.resolve("temp");

        try {
            Files.walkFileTree(source, new TrackDirectoryTree(source, vcsFolder, workingArea));
        } catch (IOException e) {
            IO.println("Impossible to track changes");
            System.out.println(e.getMessage());
            throw new IOException("impossible to complete init command");
        }
    }
}
