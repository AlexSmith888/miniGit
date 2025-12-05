package domain.services;

import domain.entities.MIniGitRepository;
import infrastructure.encryption.PathsEncryption;
import infrastructure.entities.CommitsCacheGateway;
import infrastructure.entities.FileSystemGateway;
import infrastructure.entities.RepositoriesGateway;
import infrastructure.filesystem.Cleaner;
import infrastructure.filesystem.Copier;
import infrastructure.filesystem.Eraser;
import infrastructure.filesystem.Viewer;
import infrastructure.storage.JsonData;
import infrastructure.storage.JsonEntity;

import java.io.IOException;

public class RequestsDispatcher {
    public void assemble(MIniGitRepository entity) throws IOException {
        RequestsFactory.handler(entity.returnCommand())
                .execute(entity);
    }
    public void check (String command, String [] rows){
        ValidationFactory.returnValidation(command)
                .isValid(rows);
    }
    public void process(String[] rows, CommitsCacheGateway commitsCache
            , RepositoriesGateway repoGW
            , FileSystemGateway gw
            , Copier cp
            , Eraser er
            , Cleaner cl
            , Viewer ve
            , JsonEntity jsonFile
            , PathsEncryption enc) throws IOException {
        String command = rows[0];
        if (Requests.INIT.get().equals(command)) {
            check(command, rows);
            assemble((MIniGitRepository) new MIniGitRepository.Builder()
                    .withRawData(rows)
                    .withRepositories(repoGW)
                    .withFileSystem(gw)
                    .withCopier(cp)
                    .withCommand()
                    .withSourceDir()
                    .withSourceGitDir()
                    .withSourceGitTempDir()
                    .withSourceGitCommitDir()
                    .build());
            return;
        }
        if (Requests.TRACK.get().equals(command)) {
            check(command, rows);
            assemble((MIniGitRepository) new MIniGitRepository.Builder()
                    .withRawData(rows)
                    .withFileSystem(gw)
                    .withCopier(cp)
                    .withCleaner(cl)
                    .withCommand()
                    .withSourceDir()
                    .withSourceGitDir()
                    .withSourceGitTempDir()
                    .withSourceGitCommitDir()
                    .build());
            return;
        }
        if (Requests.UNDO.get().equals(command)) {
            check(command, rows);
            assemble((MIniGitRepository) new MIniGitRepository.Builder()
                    .withRawData(rows)
                    .withCommitsCache(commitsCache)
                    .withRepositories(repoGW)
                    .withFileSystem(gw)
                    .withEraser(er)
                    .withCommand()
                    .withSourceDir()
                    .withSourceGitDir()
                    .build());
            return;
        }
        if (Requests.COMMIT.get().equals(command)) {
            check(command, rows);
            assemble((MIniGitRepository) new MIniGitRepository.Builder()
                    .withRawData(rows)
                    .withCommitsCache(commitsCache)
                    .withFileSystem(gw)
                    .withCopier(cp)
                    .withJson(jsonFile)
                    .withHash(enc)
                    .withCommand()
                    .withMetaFile()
                    .withSourceDir()
                    .withSourceGitDir()
                    .withSourceGitTempDir()
                    .withSourceGitCommitDir()
                    .withCommitMessage()
                    .build());
            return;
        }
        if (Requests.HISTORY.get().equals(command)) {
            check(command, rows);
            assemble((MIniGitRepository) new MIniGitRepository.Builder()
                    .withRawData(rows)
                    .withCommitsCache(commitsCache)
                    .withFileSystem(gw)
                    .withCommand()
                    .withSourceDir()
                    .withSourceGitCommitDir()
                    .withMetaFile()
                    .build());
            return;
        }
        if (Requests.DIFF.get().equals(command)) {
            check(command, rows);
            assemble((MIniGitRepository) new MIniGitRepository.Builder()
                    .withRawData(rows)
                    .withFileSystem(gw)
                    .withViewer(ve)
                    .withCommand()
                    .withSourceDir()
                    .withMetaFile()
                    .withSourceGitCommitDir()
                    .withCommitName1()
                    .withCommitName2()
                    .build());
            return;
        }
        if (Requests.RESTORE.get().equals(command)) {
            check(command, rows);
            assemble((MIniGitRepository) new MIniGitRepository.Builder()
                    .withRawData(rows)
                    .withCommitsCache(commitsCache)
                    .withFileSystem(gw)
                    .withCopier(cp)
                    .withCleaner(cl)
                    .withEraser(er)
                    .withCommand()
                    .withSourceDir()
                    .withMetaFile()
                    .withSourceGitCommitDir()
                    .withSourceGitDir()
                    .withCommitName1()
                    .withSourceGitTempDir()
                    .build());
            return;
        }
    }
}
