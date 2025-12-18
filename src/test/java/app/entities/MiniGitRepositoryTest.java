package app.entities;

import app.state.RequestState;
import domain.entities.MIniGitRepository;
import infrastructure.encryption.PathsEncryption;
import infrastructure.entities.CommitsCacheGateway;
import infrastructure.entities.FileSystemGateway;
import infrastructure.entities.RepositoriesGateway;
import infrastructure.filesystem.*;
import infrastructure.storage.JsonEntity;
import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests builder-derived invariants of MIniGitRepository.
 */
class MIniGitRepositoryTest {

    private MIniGitRepository buildRepo(String[] raw) {
        return (MIniGitRepository) new MIniGitRepository.Builder()
                .withRawData(raw)
                .withCommand()
                .withSourceDir()
                .withSourceGitDir()
                .withSourceGitTempDir()
                .withSourceGitCommitDir()
                .withCommitMessage()
                .withCommitName1()
                .withCommitName2()
                .withMetaFile()
                .withBackup()
                .withBackupDirectoryData()
                .withBackupCommits()
                .withBackupRepos()
                // inject infra
                .withFileSystem(mock(FileSystemGateway.class))
                .withCommitsCache(mock(CommitsCacheGateway.class))
                .withRepositories(mock(RepositoriesGateway.class))
                .withCopier(mock(Copier.class))
                .withCleaner(mock(Cleaner.class))
                .withEraser(mock(Eraser.class))
                .withViewer(mock(Viewer.class))
                .withJson(new JsonEntity())
                .withHash(mock(PathsEncryption.class))
                .withState(mock(RequestState.class))
                .withLogger(mock(Logger.class))
                .build();
    }

    @Test
    void builder_derives_paths_correctly_from_raw_input() {
        String[] raw = {"commit", "/project", "msg", "parent"};

        MIniGitRepository repo = buildRepo(raw);

        assertEquals(Path.of("/project"), repo.returnSourceDir());
        assertEquals(Path.of("/project/miniGit"), repo.returnSourceGitDir());
        assertEquals(Path.of("/project/miniGit/temp"), repo.returnSourceGitTempDir());
        assertEquals(Path.of("/project/miniGit/commits"), repo.returnSourceGitCommitDir());
    }

    @Test
    void builder_extracts_command_and_commit_message() {
        String[] raw = {"commit", "/repo", "hello", "x"};

        MIniGitRepository repo = buildRepo(raw);

        assertEquals("commit", repo.returnCommand());
        assertEquals("hello", repo.returnCommitMessage());
    }

    @Test
    void builder_sets_commit_identifiers_from_raw_input() {
        String[] raw = {"commit", "/repo", "short", "long"};

        MIniGitRepository repo = buildRepo(raw);

        assertEquals("short", repo.returnCommitShort1());
        assertEquals("long", repo.returnCommitShort2());
    }

    @Test
    void builder_creates_backup_paths_relative_to_source() {
        String[] raw = {"commit", "/root/project", "m", "p"};

        MIniGitRepository repo = buildRepo(raw);

        Path backup = repo.returnBackupDirectory();
        assertTrue(backup.toString().endsWith("/backup"));

        assertEquals(
                Path.of(backup + "/commits.txt"),
                repo.returnBackupCommitsFile()
        );

        assertEquals(
                Path.of(backup + "/repos.txt"),
                repo.returnBackupReposFile()
        );
    }

    @Test
    void injected_dependencies_are_exposed_unchanged() {
        FileSystemGateway fs = mock(FileSystemGateway.class);
        CommitsCacheGateway commits = mock(CommitsCacheGateway.class);
        RepositoriesGateway repos = mock(RepositoriesGateway.class);

        String[] raw = {"init", "/repo", "m", "p"};

        MIniGitRepository repo = (MIniGitRepository) new MIniGitRepository.Builder()
                .withRawData(raw)
                .withCommand()
                .withSourceDir()
                .withSourceGitDir()
                .withFileSystem(fs)
                .withCommitsCache(commits)
                .withRepositories(repos)
                .build();

        assertSame(fs, repo.returnFileSystem());
        assertSame(commits, repo.returnCommitsCache());
        assertSame(repos, repo.returnRepos());
    }

    @Test
    void raw_request_is_preserved() {
        String[] raw = {"restore", "/repo", "x", "y"};

        MIniGitRepository repo = buildRepo(raw);

        assertSame(raw, repo.returnSourceRawRequest());
    }
}
