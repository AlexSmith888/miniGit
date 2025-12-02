package domain.services;

import app.usecases.*;
import domain.entities.MIniGitRepository;

import java.io.IOException;

public class RequestsFactory {
    static public Request handler(String command) {
        if (Requests.INIT.get().equals(command)) {
            return new Init();
        }
        if (Requests.TRACK.get().equals(command)) {
            return new Track();
        }
        if (Requests.UNDO.get().equals(command)) {
            return new Undo();
        }
        if (Requests.COMMIT.get().equals(command)) {
            return new Commit();
        }
        if (Requests.HISTORY.get().equals(command)) {
            return new History();
        }
        if (Requests.DIFF.get().equals(command)) {
            return new Diff();
        }
        if (Requests.RESTORE.get().equals(command)) {
            return new Restore();
        }
        return new Request() {
            @Override
            public void execute(MIniGitRepository entity) throws IOException {

            }
        };
    }
}
