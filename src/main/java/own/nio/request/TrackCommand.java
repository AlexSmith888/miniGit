package own.nio.request;

import own.nio.core.Command;
import own.nio.core.MIniGitClass;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TrackCommand implements Command {
    @Override
    public void execute(MIniGitClass entity) throws IOException {
        Path source = entity.returnSourceDir();
        Path vcsFolder = entity.returnSourceGitDir();
        Path workingArea = entity.returnSourceGitTempDir();
        try {
            //TRAVERSE THE TREE TO TRACK DELETED
            Files.walkFileTree(source, new TrackDirectoryTree(source, vcsFolder, workingArea));
            Files.walkFileTree(workingArea, new DeleteDirectoryTree(workingArea, source));
        } catch (IOException e) {
            IO.println("Impossible to track changes");
            System.out.println(e.getMessage());
            throw new IOException("impossible to complete init command");
        }
    }
}
