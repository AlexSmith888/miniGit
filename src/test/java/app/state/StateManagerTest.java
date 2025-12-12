package app.state;

import domain.entities.MIniGitRepository;
import domain.services.*;

import infrastructure.cache.CachedRepositories;
import infrastructure.cache.CommitsCacheUseCases;
import infrastructure.entities.LocalFsTasksExecutor;
import infrastructure.entities.RepositoriesGateway;
import infrastructure.filesystem.Cleaner;
import infrastructure.filesystem.Copier;
import infrastructure.filesystem.Eraser;
import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure unit tests for StateManager.
 * All I/O interactions are mocked.
 */
public class StateManagerTest {

    private StateManager stateManager;

    private MIniGitRepository repo;
    private LocalFsTasksExecutor fs;
    private Copier copier;
    private Cleaner cleaner;
    private Logger logger;
    private CommitsCacheUseCases commitsCache;
    private CachedRepositories repos;
    private Eraser eraser;

    @BeforeEach
    void setup() {
        stateManager = new StateManager();

        repo = mock(MIniGitRepository.class);
        fs = mock(LocalFsTasksExecutor.class);
        copier = mock(Copier.class);
        cleaner = mock(Cleaner.class);
        logger = mock(Logger.class);
        commitsCache = mock(CommitsCacheUseCases.class);
        repos = mock(CachedRepositories.class);
        eraser = mock(Eraser.class);

        when(repo.returnFileSystem()).thenReturn(fs);
        when(repo.returnCopier()).thenReturn(copier);
        when(repo.returnCleaner()).thenReturn(cleaner);
        when(repo.returnLogger()).thenReturn(logger);
        when(repo.returnCommitsCache()).thenReturn(commitsCache);
        when(repo.returnRepos()).thenReturn(repos);
        when(repo.returnEraser()).thenReturn(eraser);

        when(repo.returnBackupDirectory()).thenReturn(Path.of("backup"));
        when(repo.returnBackupDataDirectory()).thenReturn(Path.of("backup/data"));
        when(repo.returnBackupCommitsFile()).thenReturn(Path.of("backup/commits.txt"));
        when(repo.returnBackupReposFile()).thenReturn(Path.of("backup/repos.txt"));
        when(repo.returnSourceDir()).thenReturn(Path.of("project"));
    }

    // -------------------------------------------------------------------------
    // SAVE STATE
    // -------------------------------------------------------------------------

    @Test
    void saveCurrentState_creates_backup_and_writes_cache() throws IOException {
        HashMap<String, String> map = new HashMap<>();
        map.put("c1", "parent");
        map.put("parent", "");
        when(commitsCache.retrieveSubtree("project")).thenReturn(map);

        List<Path> dirs = List.of(Path.of("project"), Path.of("extra"));
        when(repos.returnCachedDirectories()).thenReturn(dirs);

        stateManager.saveCurrentState(repo);

        // backup dirs created
        verify(fs).createDir(Path.of("backup"));
        verify(fs).createDir(Path.of("backup/data"));

        // copy project → backup/data
        verify(copier).setSource(Path.of("project"));
        verify(copier).setTarget(Path.of("backup/data"));
        verify(fs).copyRecursively(Path.of("project"), copier);

        // create files
        verify(fs).createFile(Path.of("backup/commits.txt"));
        verify(fs).createFile(Path.of("backup/repos.txt"));

        // commit cache flushed
        verify(fs).appendArowToTheFile(Path.of("backup/commits.txt"), "c1 parent");
        verify(fs).appendArowToTheFile(Path.of("backup/commits.txt"), "parent ");

        // dirs flushed (NOTE: original class writes INTO commits file – preserved intentionally)
        verify(fs).appendArowToTheFile(Path.of("backup/commits.txt"), "project");
        verify(fs).appendArowToTheFile(Path.of("backup/commits.txt"), "extra");

    }

    // -------------------------------------------------------------------------
    // RECOVER
    // -------------------------------------------------------------------------

    @Test
    void recover_restores_project_and_rebuilds_caches() throws IOException {
        // Fake commit snapshot map
        // curr="project" → "c1", "c1" -> "" → queue ends
        Map<String,String> commitsSnapshot = new HashMap<>();
        commitsSnapshot.put("project", "c1");
        commitsSnapshot.put("c1", "");

        // Fake directories snapshot
        List<Path> dirsSnapshot = List.of(Path.of("project"), Path.of("saved"));

        // Inject snapshot into private fields via reflection (StateManager stores these internally)
        // Direct assignment is OK since fields are package-private in your class
        // but safest is reflection:
        try {
            var commitsField = StateManager.class.getDeclaredField("commits");
            commitsField.setAccessible(true);
            commitsField.set(stateManager, commitsSnapshot);

            var dirsField = StateManager.class.getDeclaredField("dirs");
            dirsField.setAccessible(true);
            dirsField.set(stateManager, dirsSnapshot);
        } catch (Exception e) {
            fail("Reflection failed: " + e.getMessage());
        }

        // Recent repo cache (different to test removal/add)
        when(repos.returnCachedDirectories())
                .thenReturn(new ArrayList<>(List.of(Path.of("project"), Path.of("temp"))));

        stateManager.recoverPreviousState(repo);

        // remove backup files
        verify(fs).removeFile(Path.of("backup/commits.txt"));
        verify(fs).removeFile(Path.of("backup/repos.txt"));

        // delete project
        verify(cleaner).setSource(Path.of("project"));
        verify(fs).deleteRecursively(Path.of("project"), cleaner);

        // restore project
        verify(copier).setSource(Path.of("backup/data"));
        verify(copier).setTarget(Path.of("project"));
        verify(fs).copyRecursively(Path.of("backup/data"), copier);

        // commits rebuilt
        verify(commitsCache).removeCommitsTree(Path.of("project"));
        verify(commitsCache).addCommitToTree(Path.of("project"), "project");
        verify(commitsCache).addCommitToTree(Path.of("project"), "c1");

        // repos cache restored
        verify(repos).removeFromCache(Path.of("temp"));
        verify(repos).addToCache(Path.of("saved"));

    }

    // -------------------------------------------------------------------------
    // CLEAN
    // -------------------------------------------------------------------------

    @Test
    void clean_erases_backup_and_resets_memory() throws IOException {
        // Set internal state via reflection
        try {
            var commitsField = StateManager.class.getDeclaredField("commits");
            commitsField.setAccessible(true);
            commitsField.set(stateManager, new HashMap<>(Map.of("x", "y")));

            var dirsField = StateManager.class.getDeclaredField("dirs");
            dirsField.setAccessible(true);
            dirsField.set(stateManager, new ArrayList<>(List.of(Path.of("project"))));
        } catch (Exception e) {
            fail("Reflection failed: " + e.getMessage());
        }

        stateManager.clean(repo);

        verify(fs).eraseRecursively(Path.of("backup"), eraser);

        // Validate memory reset
        try {
            var commitsField = StateManager.class.getDeclaredField("commits");
            commitsField.setAccessible(true);
            var value = (Map<?,?>) commitsField.get(stateManager);
            assertTrue(value.isEmpty());

            var dirsField = StateManager.class.getDeclaredField("dirs");
            dirsField.setAccessible(true);
            var list = (List<?>) dirsField.get(stateManager);
            assertTrue(list.isEmpty());

        } catch (Exception e) {
            fail("Reflection failed: " + e.getMessage());
        }
    }
}