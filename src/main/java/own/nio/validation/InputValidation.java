package own.nio.validation;

import own.nio.core.Commands;
import own.nio.core.Validation;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class InputValidation implements Validation {

    public boolean isCommandExists(String value) {
        for (var current : Commands.values()) {
            if (current.get().equals(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void isValid(String [] item) throws IllegalArgumentException {
        if (item.length == 0) {
            throw new ClassCastException("An empty command");
        }

        if (!isCommandExists(item[0])) {
            throw new IllegalArgumentException("Only supported commands are allowed");
        }
    }
}
