package own.nio.validation;

import own.nio.core.Validation;
import own.nio.utils.CachedCommitTrees;

import java.nio.file.Files;
import java.nio.file.Path;

public class RestoreValidation implements Validation {
    @Override
    public void isValid(String[] item) throws IllegalArgumentException {
        if (item.length < 3) {
            throw new IllegalArgumentException("Requires a second parameter. " +
                    "Choose a directory to be restored");
        }

        if (!CachedCommitTrees.returnTrees().containsKey(item[2].trim())) {
            throw new IllegalArgumentException("Commit does not exists, nothing to restore");
        }
    }
}
