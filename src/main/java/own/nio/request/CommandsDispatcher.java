package own.nio.request;

import own.nio.core.Command;
import own.nio.core.Commands;
import own.nio.core.Validation;
import own.nio.validation.FactoryValidation;
import own.nio.validation.InitValidation;

import java.io.IOException;

public class CommandsDispatcher {
    public void process(String[] rows) throws IOException {
        String command = rows[0];
        if (Commands.INIT.get().equals(command)) {
            FactoryValidation.returnValidation(command).isValid(rows);
            RequestsFactory.handler(command).execute(rows);
            return;
        }
        if (Commands.TRACK.get().equals(command)) {
            FactoryValidation.returnValidation(command).isValid(rows);
            RequestsFactory.handler(command).execute(rows);
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
