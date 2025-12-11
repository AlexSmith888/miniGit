package app.useCases;

import app.state.StateManager;
import app.usecases.Track;
import domain.entities.MIniGitRepository;
import domain.services.*;

import infrastructure.entities.LocalFsTasksExecutor;
import infrastructure.filesystem.Cleaner;
import infrastructure.filesystem.Copier;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Track use case.
 * All external systems (FS / Copier / Cleaner / State / Logger) are mocked.
 */
public class TrackTest {

    private Track track;

    private MIniGitRepository repo;
    private LocalFsTasksExecutor fs;
    private Cleaner cleaner;
    private Copier copier;
    private StateManager state;
    private Logger logger;

    @BeforeEach
    void setup() {
        track = new Track();

        repo = mock(MIniGitRepository.class);
        fs = mock(LocalFsTasksExecutor.class);
        cleaner = mock(Cleaner.class);
        copier = mock(Copier.class);
        state = mock(StateManager.class);
        logger = mock(Logger.class);

        // return dependencies
        when(repo.returnFileSystem()).thenReturn(fs);
        when(repo.returnCleaner()).thenReturn(cleaner);
        when(repo.returnCopier()).thenReturn(copier);
        when(repo.returnState()).thenReturn(state);
        when(repo.returnLogger()).thenReturn(logger);

        when(repo.returnSourceDir()).thenReturn(Path.of("project"));
        when(repo.returnSourceGitDir()).thenReturn(Path.of("miniGit"));
        when(repo.returnSourceGitTempDir()).thenReturn(Path.of("miniGit/temp"));
    }

    @Test
    void track_success_flow_executes_copy_clean_operations() throws IOException {
        track.execute(repo);

        verify(state).saveCurrentState(repo);

        // cleaner
        verify(cleaner).setSource(Path.of("miniGit/temp"));
        verify(fs).deleteRecursively(Path.of("miniGit/temp"), cleaner);

        // copier
        verify(copier).setSource(Path.of("project"));
        verify(copier).setTarget(Path.of("miniGit/temp"));
        verify(copier).addToExcludedList(Path.of("miniGit"));
        verify(fs).copyRecursively(Path.of("project"), copier);
        verify(copier).truncateExcludedList();

        verify(state).clean(repo);
        verify(logger, never()).error((Message) any());
    }

    @Test
    void track_failure_during_clean_or_copy_triggers_recovery_and_logs() throws IOException {
        doThrow(new IOException("fs fail"))
                .when(fs).deleteRecursively(any(), any());

        assertThrows(IOException.class, () -> track.execute(repo));

        verify(logger).error("Impossible to track changes");
        verify(logger).error("fs fail");

        verify(state).recoverPreviousState(repo);
        verify(state).clean(repo);
    }

    @Test
    void track_failure_during_copy_triggers_rollback() throws IOException {
        doNothing().when(fs).deleteRecursively(any(), any());
        doThrow(new IOException("copy failed"))
                .when(fs).copyRecursively(any(), any());

        assertThrows(IOException.class, () -> track.execute(repo));

        verify(logger).error("Impossible to track changes");
        verify(logger).error("copy failed");
        verify(state).recoverPreviousState(repo);
        verify(state).clean(repo);
    }

    @Test
    void always_cleans_after_success() throws IOException {
        track.execute(repo);
        verify(state).clean(repo);
    }
}