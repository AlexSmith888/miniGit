package app.validations;

import domain.validators.Validation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class HistoryValidation implements Validation {
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

        Path mainGitDir = directory.resolve("miniGit");
        if (!Files.isDirectory(mainGitDir)) {
            throw new IllegalArgumentException("Should be under miniGit control");
        }

        try {
            List<Path> arr
                    = Files.list(Path.of(mainGitDir + "/commits")).toList();
            if (arr.isEmpty()) {
                throw new IllegalArgumentException("A commit tree does not have any commits yet");
            }
        } catch (IOException e) {
            System.out.println("Impossible to read a commit folder");
            e.printStackTrace();
        }
    }
}
