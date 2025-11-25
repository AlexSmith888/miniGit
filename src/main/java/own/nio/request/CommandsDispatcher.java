package own.nio.request;

import own.nio.core.Commands;
import own.nio.validation.ValidationFactory;

import java.io.IOException;

public class CommandsDispatcher {
    public void assemble(String[] rows) throws IOException {
        String command = rows[0];
        ValidationFactory.returnValidation(command).isValid(rows);
        CommandsFactory.handler(command).execute(rows);
    }
    public void process(String[] rows) throws IOException {
        String command = rows[0];
        if (Commands.INIT.get().equals(command)) {
            assemble(rows);
            return;
        }
        if (Commands.TRACK.get().equals(command)) {
            assemble(rows);
            return;
        }
        if (Commands.UNDO.get().equals(command)) {
            assemble(rows);
            return;
        }
        /*if (Commands.COMMIT.get().equals(command)) {
            return;
        }
        if (Commands.RESTORE.get().equals(command)) {
            return;
        }
        if (Commands.DIFF.get().equals(command)) {
            return;
        }*/
    }
}
