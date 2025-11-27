package own.nio.request;

import own.nio.core.Command;
import own.nio.utils.EncryptCommitPaths;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class CommitCommand implements Command {

    @Override
    public void execute(Object[] items) throws IOException {
        String[] arr = (String[]) items;

        Path source = Path.of(arr[1]);
        Path vcsFolder = source.resolve("miniGit");
        Path workingArea = vcsFolder.resolve("temp");
        Path commitArea = vcsFolder.resolve("commits");

        EncryptCommitPaths.process(source);
        String shortIndentifier =  EncryptCommitPaths.getShortHash();
        String longIndentifier = EncryptCommitPaths.getLongHash();
        String full = EncryptCommitPaths.getHash();

        Path mainCommitDirectory = commitArea.resolve(shortIndentifier);
        Path mainDataCommitDirectory = mainCommitDirectory.resolve(longIndentifier);

        try {
            //Files.walkFileTree(source, new TrackDirectoryTree(source, vcsFolder, workingArea));
            //Files.walkFileTree(workingArea, new DeleteDirectoryTree(workingArea, source));
            Files.createDirectory(mainCommitDirectory);
            Files.createDirectory(mainDataCommitDirectory);
            Files.walkFileTree(workingArea,
                    new MoveCommitTree(workingArea, "temp",
                            "commits" + "/" + shortIndentifier + "/" + longIndentifier));
        } catch (IOException e) {
            IO.println("Impossible to commit changes");
            System.out.println(e.getMessage());
            throw e;
        }
    }
}
