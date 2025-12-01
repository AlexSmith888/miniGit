package own.nio.request;

import own.nio.core.Command;
import own.nio.core.Commands;
import own.nio.core.MIniGitClass;

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
        if (Commands.COMMIT.get().equals(command)) {
            return new CommitCommand();
        }
        if (Commands.HISTORY.get().equals(command)) {
            return new HistoryCommand();
        }
        if (Commands.DIFF.get().equals(command)) {
            return new DiffCommand();
        }
        if (Commands.RESTORE.get().equals(command)) {
            return new RestoreCommand();
        }
        return new Command() {
            @Override
            public void execute(MIniGitClass entity) throws IOException {

            }
        };
    }
}
