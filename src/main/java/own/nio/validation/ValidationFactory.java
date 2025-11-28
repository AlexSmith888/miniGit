package own.nio.validation;

import own.nio.core.Commands;
import own.nio.core.Validation;

public class ValidationFactory {
    public static Validation returnValidation(String command) {
        if (Commands.INIT.get().equals(command)) {
            return new InitValidation();
        }
        if (Commands.TRACK.get().equals(command)) {
            return new TrackValidation();
        }
        if (Commands.UNDO.get().equals(command)) {
            return new UndoValidation();
        }
        if (Commands.COMMIT.get().equals(command)) {
            return new CommitValidation();
        }
        if (Commands.HISTORY.get().equals(command)) {
            return new HistoryValidation();
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
