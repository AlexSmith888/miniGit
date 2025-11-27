package own.nio.validation;

import own.nio.core.Validation;

import java.nio.file.Files;
import java.nio.file.Path;

public class CommitValidation implements Validation {
    @Override
    public void isValid(String[] item) throws IllegalArgumentException {
        if (item.length < 2) {
            throw new IllegalArgumentException("Requires a second parameter. " +
                    "Choose a directory to be watched");
        }

        Path directory = Path.of(item[1]);
        if (!Files.isDirectory(directory)) {
            throw new IllegalArgumentException("Should be an existing directory");
        }

        if (!Files.isDirectory(Path.of(directory + "/miniGit"))) {
            throw new IllegalArgumentException("Should be under miniGit control");
        }

    }
}
