package own.nio.validation;

import own.nio.core.Commands;
import own.nio.core.Validation;
import own.nio.request.MiniGitInit;

public class FactoryValidation {
    public static Validation returnValidation(String command) {
        if (Commands.INIT.get().equals(command)) {
            return new InitValidation();
        }
        if (Commands.TRACK.get().equals(command)) {
            return new TrackValidation();
        }
        if (Commands.COMMIT.get().equals(command)) {
            return new CommitValidation();
        }
        if (Commands.RESTORE.get().equals(command)) {
            return new RestoreValidation();
        }
        if (Commands.DIFF.get().equals(command)) {
            return new DiffValidation();
        }
        return new Validation(){
            @Override
            public void isValid(String[] item) throws IllegalArgumentException {

            }
        };
    }
}
