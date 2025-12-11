package app.validations;

import domain.validators.Validation;

import java.nio.file.Files;
import java.nio.file.Path;

public class CommitValidation implements Validation {
    @Override
    public void isValid(String[] item) throws IllegalArgumentException {
        if (item.length < 3) {
            throw new IllegalArgumentException("Requires a {commit}" +
                    "{path}{\"message - either blank or not\"}}");
        }

        Path directory = Path.of(item[1]);
        if (!Files.isDirectory(directory)) {
            throw new IllegalArgumentException("Should be an existing directory");
        }

        Path mainGitDir = directory.resolve("miniGit");
        if (!Files.isDirectory(mainGitDir)) {
            throw new IllegalArgumentException("Should be under miniGit control");
        }
    }
}
