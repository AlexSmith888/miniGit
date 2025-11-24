package own.nio.request;

import own.nio.core.Command;
import own.nio.core.Commands;
import own.nio.validation.CommitValidation;
import own.nio.validation.DiffValidation;
import own.nio.validation.InitValidation;
import own.nio.validation.RestoreValidation;

import java.io.IOException;

public class RequestsFactory {
    static public Command handler(String command) {
        if (Commands.INIT.get().equals(command)) {
            return new MiniGitInit();
        }
        if (Commands.TRACK.get().equals(command)) {
            return new TrackCommand();
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
