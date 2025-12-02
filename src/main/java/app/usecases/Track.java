package app.usecases;

import domain.services.Request;
import domain.entities.MIniGitRepository;
import infrastructure.filesystem.DeleteDirectoryTree;
import infrastructure.filesystem.TrackDirectoryTree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Track implements Request {
    @Override
    public void execute(MIniGitRepository entity) throws IOException {
        Path source = entity.returnSourceDir();
        Path vcsFolder = entity.returnSourceGitDir();
        Path workingArea = entity.returnSourceGitTempDir();
        try {
            //TRAVERSE THE TREE TO TRACK DELETED
            Files.walkFileTree(source, new TrackDirectoryTree(source, vcsFolder, workingArea));
            Files.walkFileTree(workingArea, new DeleteDirectoryTree(workingArea, source));
        } catch (IOException e) {
            System.out.println("Impossible to track changes");
            System.out.println(e.getMessage());
            throw new IOException("impossible to complete init command");
        }
    }
}
