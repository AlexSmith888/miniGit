package app.validations;

import domain.validators.Validation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DiffValidation implements Validation {
    @Override
    public void isValid(String[] item) throws IllegalArgumentException {
        if (item.length < 4) {
            throw new IllegalArgumentException("Requires a command, directory" +
                    ", and two short commit names");
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
            if (arr.size() < 2) {
                throw new IllegalArgumentException("Only one commit persists in the commit folder");
            }
            if (!Files.exists(Path.of(mainGitDir + "/commits" + "/" + item[2]))) {
                throw new IOException("Both commits should exist");
            }
            if (!Files.exists(Path.of(mainGitDir + "/commits" + "/" + item[3]))) {
                throw new IOException("Both commits should exist");
            }
        } catch (IOException e) {
            System.out.println("Impossible to read a commit folder");
            e.printStackTrace();
        }
    }
}
