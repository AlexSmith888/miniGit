package own.nio.validation;

import own.nio.core.Validation;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class UndoValidation implements Validation {
    @Override
    public void isValid(String[] item) throws IllegalArgumentException {
        if (item.length < 2) {
            throw new IllegalArgumentException("Should be two parameters");
        }
        Path source = Path.of(item[1]);
        Path vcsFolder = source.resolve("miniGit");
        Path workingArea = vcsFolder.resolve("temp");
        Path commitsTree = vcsFolder.resolve("commits");

        if (!Files.exists(vcsFolder)
                || !Files.exists(workingArea)
                || !Files.exists(commitsTree)
        ) {
            throw new IllegalArgumentException("Should be under miniGit control");
        }
    }
}
