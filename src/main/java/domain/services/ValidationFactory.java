package domain.services;

import app.validations.*;
import domain.validators.Validation;

public class ValidationFactory {
    public static Validation returnValidation(String command) {
        if (Requests.INIT.get().equals(command)) {
            return new InitValidation();
        }
        if (Requests.TRACK.get().equals(command)) {
            return new TrackValidation();
        }
        if (Requests.UNDO.get().equals(command)) {
            return new UndoValidation();
        }
        if (Requests.COMMIT.get().equals(command)) {
            return new CommitValidation();
        }
        if (Requests.HISTORY.get().equals(command)) {
            return new HistoryValidation();
        }
        if (Requests.RESTORE.get().equals(command)) {
            return new RestoreValidation();
        }
        if (Requests.DIFF.get().equals(command)) {
            return new DiffValidation();
        }
        return new Validation(){
            @Override
            public void isValid(String[] item) throws IllegalArgumentException {
            }
        };
    }
}
