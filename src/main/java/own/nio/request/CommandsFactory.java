package own.nio.request;

import own.nio.core.Command;
import own.nio.core.Commands;

import java.io.IOException;

public class CommandsFactory {
    static public Command handler(String command) {
        if (Commands.INIT.get().equals(command)) {
            return new InitCommand();
        }
        if (Commands.TRACK.get().equals(command)) {
            return new TrackCommand();
        }
        if (Commands.UNDO.get().equals(command)) {
            return new UndoCommand();
        }
        /*if (Commands.COMMIT.get().equals(command)) {
            return new ;
        }
        if (Commands.RESTORE.get().equals(command)) {
            return new RestoreValidation();
        }
        if (Commands.DIFF.get().equals(command)) {
            return new DiffValidation();
        }*/
        return new Command() {
            @Override
            public void execute(Object[] items) throws IOException {

            }
        };
    }
}
