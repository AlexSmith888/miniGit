package app.usecases;

import app.state.StateManager;
import domain.entities.MIniGitRepository;
import domain.services.*;

import infrastructure.cache.CommitsCacheUseCases;
import infrastructure.entities.CommitsCacheGateway;
import infrastructure.entities.LocalFsTasksExecutor;
import infrastructure.filesystem.Cleaner;
import infrastructure.filesystem.Copier;
import infrastructure.filesystem.Eraser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

import org.apache.logging.log4j.core.Logger;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Restore use case.
 * Tests the full control flow using pure mocks (no real filesystem interaction).
 */
public class RestoreTest {

    private Restore restore;

    private MIniGitRepository repo;
    private LocalFsTasksExecutor fs;
    private Cleaner cleaner;
    private Copier copier;
    private StateManager state;
    private org.apache.logging.log4j.core.Logger logger;
    private CommitsCacheUseCases commits;
    private Eraser eraser;

    @BeforeEach
    void setup() throws IOException {
        restore = new Restore();

        repo = mock(MIniGitRepository.class);
        fs = mock(LocalFsTasksExecutor.class);
        cleaner = mock(Cleaner.class);
        copier = mock(Copier.class);
        state = mock(StateManager.class);
        logger = mock(Logger.class);
        commits = mock(CommitsCacheUseCases.class);
        eraser = mock(Eraser.class);

        when(repo.returnFileSystem()).thenReturn(fs);
        when(repo.returnCleaner()).thenReturn(cleaner);
        when(repo.returnCopier()).thenReturn(copier);
        when(repo.returnState()).thenReturn(state);
        when(repo.returnLogger()).thenReturn(logger);
        when(repo.returnCommitsCache()).thenReturn(commits);
        when(repo.returnEraser()).thenReturn(eraser);

        when(repo.returnCommitShort1()).thenReturn("abc123");
        when(repo.returnSourceGitCommitDir()).thenReturn(Path.of("miniGit/commits"));
        when(repo.returnSourceGitTempDir()).thenReturn(Path.of("miniGit/temp"));
        when(repo.returnSourceDir()).thenReturn(Path.of("project"));
        when(repo.returnSourceGitDir()).thenReturn(Path.of("miniGit"));
        when(repo.returnMetaFile()).thenReturn("meta.json");

        // Path to commit's metadata (via returnfullPath)
        when(fs.returnfullPath(any(), any()))
                .thenAnswer(inv -> Path.of(inv.getArgument(0).toString()));
    }

    @Test
    void restore_success_flow_executes_all_steps_correctly() throws IOException {
        // Fake commit tree for erase logic:
        HashMap<String,String> map = new HashMap<>();
        map.put("abc123", "parent1");
        map.put("parent1", ""); // end of chain
        when(commits.retrieveSubtree("abc123")).thenReturn(map);

        restore.execute(repo);

        // State saved first
        verify(state).saveCurrentState(repo);

        // 1st cleaning step: clean temp folder
        verify(cleaner).setSource(Path.of("miniGit/temp"));
        verify(fs).deleteRecursively(Path.of("miniGit/temp"), cleaner);

        // Copy commit content → temp
        verify(copier).setSource(Path.of("miniGit/commits/abc123"));
        verify(copier).setTarget(Path.of("miniGit/temp"));
        verify(fs).copyRecursively(Path.of("miniGit/commits/abc123"), copier);

        // Clean project except .minigit
        verify(cleaner).setSource(Path.of("project"));
        verify(cleaner).addToExcludedList(Path.of("miniGit"));
        verify(fs).deleteRecursively(Path.of("project"), cleaner);
        verify(cleaner).truncateExcludedList();

        // Copy temp → project
        verify(copier).setSource(Path.of("miniGit/temp"));
        verify(copier).setTarget(Path.of("project"));
        verify(fs).copyRecursively(Path.of("miniGit/temp"), copier);

        // Commit tree operations
        verify(commits).removeCommitsSubTree("abc123",
                Path.of("miniGit/commits"), "meta.json");

        // Parent erasing sequence:
        verify(fs).eraseRecursively(Path.of("miniGit/commits/parent1"), eraser);

        // Cleanup always executed
        verify(state).clean(repo);

        // No rollback logging
        verify(logger, never()).error(anyString(), (Throwable) any());
    }

    @Test
    void restore_failure_triggers_rollback_and_logs() throws IOException {
        // Force failure early (deleteRecursively fails)
        doThrow(new IOException("boom"))
                .when(fs).deleteRecursively(any(), any());

        assertThrows(IOException.class, () -> restore.execute(repo));

        verify(logger).error("Impossible to restore repo to the state of {}", "abc123");
        verify(logger).error("boom");

        verify(state).recoverPreviousState(repo);
        verify(state).clean(repo);
    }

    @Test
    void restore_failure_during_copy_triggers_recovery() throws IOException {
        // First delete works, second copy fails
        doNothing().when(fs).deleteRecursively(any(), any());
        doThrow(new IOException("copy fail"))
                .when(fs).copyRecursively(any(), any());

        assertThrows(IOException.class, () -> restore.execute(repo));

        verify(logger).error("Impossible to restore repo to the state of {}", "abc123");
        verify(logger).error("copy fail");

        verify(state).recoverPreviousState(repo);
        verify(state).clean(repo);
    }

    @Test
    void restore_erases_commit_chain_from_map() throws IOException {
        // A deeper chain map to test queue iteration
        HashMap<String,String> map = new HashMap<>();
        map.put("abc123", "p1");
        map.put("p1", "p2");
        map.put("p2", ""); // end
        when(commits.retrieveSubtree("abc123")).thenReturn(map);

        restore.execute(repo);

        // Each commit folder in chain should be erased
        verify(fs).eraseRecursively(Path.of("miniGit/commits/p1"), eraser);
        verify(fs).eraseRecursively(Path.of("miniGit/commits/p2"), eraser);
    }
}
