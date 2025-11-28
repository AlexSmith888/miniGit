package own.nio.request;

import own.nio.core.Commands;
import own.nio.core.MIniGitClass;
import own.nio.core.MiniGitEntity;
import own.nio.validation.ValidationFactory;

import java.io.IOException;

public class CommandsDispatcher {
    public void assemble(MIniGitClass entity) throws IOException {
        CommandsFactory.handler(entity.returnCommand())
                .execute(entity);
    }
    public void check (String command, String [] rows){
        ValidationFactory.returnValidation(command)
                .isValid(rows);
    }
    public void process(String[] rows) throws IOException {
        String command = rows[0];
        if (Commands.INIT.get().equals(command)) {
            check(command, rows);
            assemble((MIniGitClass) new MIniGitClass.Builder()
                            .withRawData(rows)
                            .withCommand()
                            .withSourceDir()
                            .withSourceGitDir()
                            .withSourceGitTempDir()
                            .withSourceGitCommitDir()
                            .build());
            return;
        }
        if (Commands.TRACK.get().equals(command)) {
            check(command, rows);
            assemble((MIniGitClass) new MIniGitClass.Builder()
                    .withRawData(rows)
                    .withCommand()
                    .withSourceDir()
                    .withSourceGitDir()
                    .withSourceGitTempDir()
                    .withSourceGitCommitDir()
                    .build());
            return;
        }
        if (Commands.UNDO.get().equals(command)) {
            check(command, rows);
            assemble((MIniGitClass) new MIniGitClass.Builder()
                    .withRawData(rows)
                    .withCommand()
                    .withSourceDir()
                    .withSourceGitDir()
                    .withSourceGitTempDir()
                    .withSourceGitCommitDir()
                    .build());
            return;
        }
        if (Commands.COMMIT.get().equals(command)) {
            check(command, rows);
            assemble((MIniGitClass) new MIniGitClass.Builder()
                    .withRawData(rows)
                    .withCommand()
                    .withSourceDir()
                    .withSourceGitDir()
                    .withSourceGitTempDir()
                    .withSourceGitCommitDir()
                    .withCommitMessage()
                    .build());
            return;
        }
        if (Commands.HISTORY.get().equals(command)) {
            check(command, rows);
            assemble((MIniGitClass) new MIniGitClass.Builder()
                    .withRawData(rows)
                    .withCommand()
                    .withSourceDir()
                    .withSourceGitCommitDir()
                    .withMetaFile()
                    .build());
            return;
        }
        if (Commands.DIFF.get().equals(command)) {
            check(command, rows);
            assemble((MIniGitClass) new MIniGitClass.Builder()
                    .withRawData(rows)
                    .withCommand()
                    .withSourceDir()
                    .withMetaFile()
                    .withSourceGitCommitDir()
                    .withCommitName1()
                    .withCommitName2()
                    .build());
        }
    }
}
