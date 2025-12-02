package app.validations;

import domain.services.Requests;
import domain.validators.Validation;

public class InputValidation implements Validation {

    public boolean isCommandExists(String value) {
        for (var current : Requests.values()) {
            if (current.get().equals(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void isValid(String [] item) throws IllegalArgumentException {
        if (item.length == 0) {
            throw new IllegalArgumentException("An empty command");
        }

        if (!isCommandExists(item[0])) {
            throw new IllegalArgumentException("Only supported commands are allowed");
        }
    }
}
