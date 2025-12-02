package app.validations;

import domain.validators.Validation;

import java.nio.file.Files;
import java.nio.file.Path;

public class TrackValidation implements Validation {
    @Override
    public void isValid(String[] item) throws IllegalArgumentException {
        if (item.length < 2) {
            throw new IllegalArgumentException("Insufficient parameters list");
        }
        Path source = Path.of(item[1]);
        Path vcsFolder = source.resolve("miniGit");
        Path workingArea = vcsFolder.resolve("temp");

        if (!Files.exists(vcsFolder)) {
            throw new IllegalArgumentException("Should use miniGit {init} first" +
                    ", skipping further processing");
        }
    }
}
