package own.nio.validation;

import own.nio.core.Validation;

import java.nio.file.Files;
import java.nio.file.Path;

public class InitValidation implements Validation {
    @Override
    public void isValid(String [] items) throws IllegalArgumentException {
        if (items.length < 2) {
            throw new IllegalArgumentException("Requires a second parameter. " +
                    "Choose a directory to be watched");
        }

        Path directory = Path.of(items[1]);
        if (!Files.isDirectory(directory)) {
            throw new IllegalArgumentException("Should be an existing directory");
        }

        String vcs = ".vcs";
        Path vcsFolder = Path.of(directory + "/" + vcs);
        if (Files.exists(vcsFolder)) {
            throw new IllegalArgumentException("The directory is Already under the miniGit maintenance");
        }
    }
}
