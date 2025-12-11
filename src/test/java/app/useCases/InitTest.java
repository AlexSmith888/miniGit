package app.useCases;

import app.state.StateManager;
import app.usecases.Init;
import domain.entities.MIniGitRepository;
import infrastructure.cache.CachedRepositories;
import infrastructure.entities.LocalFsTasksExecutor;
import infrastructure.filesystem.Copier;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.util.Loader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class InitTest {

    private MIniGitRepository repo;
    private LocalFsTasksExecutor fs;
    private CachedRepositories repos;
    private Copier copier;
    private StateManager state;
    private Logger logger;

    private Init initUseCase;

    @BeforeEach
    void setup() {
        // mock dependencies
        repo = mock(MIniGitRepository.class);
        fs = mock(LocalFsTasksExecutor.class);
        repos = mock(CachedRepositories.class);
        copier = mock(Copier.class);
        state = mock(StateManager.class);
        logger = mock(Logger.class);

        // return mocked getters from repo
        when(repo.returnFileSystem()).thenReturn(fs);
        when(repo.returnRepos()).thenReturn(repos);
        when(repo.returnCopier()).thenReturn(copier);
        when(repo.returnState()).thenReturn(state);
        when(repo.returnLogger()).thenReturn(logger);

        // path returns:
        when(repo.returnSourceDir()).thenReturn(Path.of("src"));
        when(repo.returnSourceGitDir()).thenReturn(Path.of("src/miniGit"));
        when(repo.returnSourceGitTempDir()).thenReturn(Path.of("src/miniGit/temp"));
        when(repo.returnSourceGitCommitDir()).thenReturn(Path.of("src/miniGit/commits"));

        // cached directories representation
        when(repos.returnCachedDirectories()).thenReturn(new java.util.ArrayList<>());

        initUseCase = new Init();
    }

    @Test
    void testInitCreatesRequiredDirectories() throws IOException {
        initUseCase.execute(repo);

        verify(state).saveCurrentState(repo);
        verify(fs).createDir(Path.of("src/miniGit"));
        verify(fs).createDir(Path.of("src/miniGit/temp"));
        verify(fs).createDir(Path.of("src/miniGit/commits"));
        verify(state).clean(repo); // final cleanup step
    }

    @Test
    void testTrackingSourceDirectoryAddedToCachedList() throws IOException {
        initUseCase.execute(repo);
        assertTrue(repos.returnCachedDirectories().contains(Path.of("src")));
    }

    @Test
    void testCopyOperationIsPerformed() throws IOException {
        initUseCase.execute(repo);

        verify(copier).setSource(Path.of("src"));
        verify(copier).setTarget(Path.of("src/miniGit/temp"));
        verify(copier).addToExcludedList(Path.of("src/miniGit"));
        verify(fs).copyRecursively(Path.of("src"), copier);
        verify(copier).truncateExcludedList();
    }

    @Test
    void testFailureTriggersRollbackAndThrows() throws IOException {
        doThrow(new IOException("fail")).when(fs).createDir(Path.of("src/miniGit"));

        assertThrows(IOException.class, ()-> initUseCase.execute(repo));

        verify(logger).error("Impossible to create a miniGit repository");
        verify(state).recoverPreviousState(repo);
        verify(state).clean(repo);
    }
}
