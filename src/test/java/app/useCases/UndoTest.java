package app.usecases;

import app.state.StateManager;
import domain.entities.MIniGitRepository;
import domain.services.*;

import infrastructure.cache.CachedRepositories;
import infrastructure.cache.CommitsCacheUseCases;
import infrastructure.entities.LocalFsTasksExecutor;
import infrastructure.filesystem.Eraser;
import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Undo use case.
 * All external operations are isolated via mocks.
 */
public class UndoTest {

    private Undo undo;

    private MIniGitRepository repo;
    private LocalFsTasksExecutor fs;
    private Eraser eraser;
    private StateManager state;
    private Logger logger;
    private CachedRepositories repos;
    private CommitsCacheUseCases commits;

    @BeforeEach
    void setup() {
        undo = new Undo();

        repo = mock(MIniGitRepository.class);
        fs = mock(LocalFsTasksExecutor.class);
        eraser = mock(Eraser.class);
        state = mock(StateManager.class);
        logger = mock(Logger.class);
        repos = mock(CachedRepositories.class);
        commits = mock(CommitsCacheUseCases.class);

        when(repo.returnFileSystem()).thenReturn(fs);
        when(repo.returnEraser()).thenReturn(eraser);
        when(repo.returnState()).thenReturn(state);
        when(repo.returnLogger()).thenReturn(logger);
        when(repo.returnRepos()).thenReturn(repos);
        when(repo.returnCommitsCache()).thenReturn(commits);

        when(repo.returnSourceGitDir()).thenReturn(Path.of("miniGit"));
        when(repo.returnSourceDir()).thenReturn(Path.of("project"));
        when(repos.returnCachedDirectories()).thenReturn(new java.util.ArrayList<>());
    }

    @Test
    void undo_success_flow_performs_full_cleanup() throws IOException {
        undo.execute(repo);

        // Save state at start
        verify(state).saveCurrentState(repo);

        // Set eraser source
        verify(eraser).setSource(Path.of("miniGit"));

        // Delete .minigit directory
        verify(fs).eraseRecursively(Path.of("miniGit"), eraser);

        // Remove project directory from cache
        //verify(repos.returnCachedDirectories()).remove(Path.of("project"));

        // Remove commit tree for project
        verify(commits).removeCommitsTree(Path.of("project"));

        // Always cleaned afterward
        verify(state).clean(repo);

        // No rollback expected
        verify(logger, never()).error(anyString());
    }

    @Test
    void undo_failure_triggers_recovery_and_logs() throws IOException {
        // Force deletion failure
        doThrow(new IOException("erase fail"))
                .when(fs).eraseRecursively(any(), any());

        assertThrows(IOException.class, () -> undo.execute(repo));

        // Logging
        verify(logger).error("Impossible to delete miniGit folder");
        verify(logger).error("erase fail");

        // Rollback
        verify(state).recoverPreviousState(repo);
        verify(state).clean(repo);
    }

    @Test
    void always_cleans_state_even_on_success() throws IOException {
        undo.execute(repo);
        verify(state).clean(repo);
    }

    @Test
    void always_saves_state_before_operations() throws IOException {
        undo.execute(repo);
        verify(state).saveCurrentState(repo);
    }
}
