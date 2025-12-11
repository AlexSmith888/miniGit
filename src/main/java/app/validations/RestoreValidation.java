package app.validations;

import domain.validators.Validation;

import java.nio.file.Files;
import java.nio.file.Path;

public class RestoreValidation implements Validation {
    @Override
    public void isValid(String[] item) throws IllegalArgumentException {
        if (item.length < 3) {
            throw new IllegalArgumentException("Insufficient parameters list");
        }

        Path directory = Path.of(item[1]);
        Path commitDir = directory.resolve("miniGit/commits/" + item[2]);
        if (!Files.exists(commitDir)) {
            throw new IllegalArgumentException("Commit does not exist");
        }

        /*if (!CachedCommitTrees.returnTrees().containsKey(item[2].trim())) {
            throw new IllegalArgumentException("Commit does not exists, nothing to restore");
        }*/
    }
}
