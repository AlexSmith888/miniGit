package app.validations;

import domain.validators.Validation;
import infrastructure.cache.CachedCommitTrees;

public class RestoreValidation implements Validation {
    @Override
    public void isValid(String[] item) throws IllegalArgumentException {
        if (item.length < 3) {
            throw new IllegalArgumentException("Insufficient parameters list");
        }

        if (!CachedCommitTrees.returnTrees().containsKey(item[2].trim())) {
            throw new IllegalArgumentException("Commit does not exists, nothing to restore");
        }
    }
}
