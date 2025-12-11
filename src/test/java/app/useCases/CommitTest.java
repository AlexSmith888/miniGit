package app.usecases;

import app.state.StateManager;
import domain.entities.MIniGitRepository;
import domain.services.*;
import infrastructure.cache.CommitsCacheUseCases;
import infrastructure.encryption.PathCipher;
import infrastructure.entities.LocalFsTasksExecutor;
import infrastructure.filesystem.Copier;
import infrastructure.storage.JsonEntity;

import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CommitTest {

    private Commit commit;

    private MIniGitRepository repo;
    private PathCipher cipher;
    private LocalFsTasksExecutor fs;
    private Copier copier;
    private StateManager state;
    private Logger logger;
    private CommitsCacheUseCases commitsCache;
    private JsonEntity json;

    @BeforeEach
    void setup() {
        commit = new Commit();

        repo = mock(MIniGitRepository.class);
        cipher = mock(PathCipher.class);
        fs = mock(LocalFsTasksExecutor.class);
        copier = mock(Copier.class);
        state = mock(StateManager.class);
        logger = mock(Logger.class);
        commitsCache = mock(CommitsCacheUseCases.class);
        json = mock(JsonEntity.class);

        when(repo.returnCipher()).thenReturn(cipher);
        when(repo.returnFileSystem()).thenReturn(fs);
        when(repo.returnState()).thenReturn(state);
        when(repo.returnCopier()).thenReturn(copier);
        when(repo.returnLogger()).thenReturn(logger);
        when(repo.returnJson()).thenReturn(json);
        when(repo.returnCommitsCache()).thenReturn(commitsCache);

        // Paths used
        when(repo.returnSourceDir()).thenReturn(Path.of("project"));
        when(repo.returnSourceGitCommitDir()).thenReturn(Path.of("miniGit/commits"));
        when(repo.returnSourceGitTempDir()).thenReturn(Path.of("miniGit/temp"));
        when(repo.returnMetaFile()).thenReturn("meta.json");

        // Hash simulation
        when(cipher.getShortHash()).thenReturn("abc123");
        when(cipher.getLongHash()).thenReturn("abc123-long");
        when(cipher.getHash()).thenReturn("abc123full");
    }

    @Test
    void commit_success_flow_creates_directories_and_writes_json() throws IOException {
        commit.execute(repo);

        // verify directory creation
        verify(state).saveCurrentState(repo);
        verify(fs).createDir(Path.of("miniGit/commits/abc123"));
        verify(fs).createDir(Path.of("miniGit/commits/abc123/abc123-long"));

        // verify copy
        verify(copier).setSource(Path.of("miniGit/temp"));
        verify(copier).setTarget(Path.of("miniGit/commits/abc123/abc123-long"));
        verify(fs).copyRecursively(Path.of("miniGit/temp"), copier);

        // verify json writing
        verify(fs).writeJsonToTheDisk(Path.of("miniGit/commits/abc123/meta.json"), json);
        verify(commitsCache).addCommitToTree(Path.of("project"), "abc123");

        // cleanup called at end
        verify(state).clean(repo);
    }

    @Test
    void commit_fails_on_dir_creation_triggers_rollback() throws IOException {
        doThrow(new IOException("dir error")).when(fs).createDir(any());

        assertThrows(IOException.class, () -> commit.execute(repo));

        verify(logger).warn("The issue while creating miniGit repositories");
        verify(state).recoverPreviousState(repo);
        verify(state).clean(repo);
    }

    @Test
    void commit_fails_on_copy_operation_triggers_rollback() throws IOException {
        // first dir creation works
        // fail later
        doNothing().when(fs).createDir(any());
        doThrow(new IOException("copy fail")).when(fs).copyRecursively(any(), any());

        assertThrows(IOException.class, () -> commit.execute(repo));

        verify(logger).error("Copying files to commit directories failed");
        verify(state).recoverPreviousState(repo);
        verify(state).clean(repo);
    }

    @Test
    void commit_fails_on_json_write_triggers_rollback() throws IOException {
        doNothing().when(fs).createDir(any());
        doNothing().when(fs).copyRecursively(any(), any());
        doThrow(new IOException("json write fail")).when(fs).writeJsonToTheDisk(any(), any());

        assertThrows(IOException.class, () -> commit.execute(repo));

        verify(logger).error("Failed to create a Json file");
        verify(state).recoverPreviousState(repo);
        verify(state).clean(repo);
    }
}