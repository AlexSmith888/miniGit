package infrastructure.cache;

import infrastructure.entities.FileSystemGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CachedRepositoriesTest {

    private FileSystemGateway gw;
    private CachedRepositories cache;

    @BeforeEach
    void setup() {
        gw = mock(FileSystemGateway.class);
        cache = new CachedRepositories(gw);
    }

    // -----------------------------------------------------------------------------------------
    // loadCachedDirs()
    // -----------------------------------------------------------------------------------------

    @Test
    void testLoadCachedDirs_initializesRootIfMissing() throws IOException {
        // Simulate "root" does NOT exist
        when(gw.isDirExists(any())).thenReturn(false);

        cache.loadCachedDirs();

        verify(gw).createDir(any());
        verify(gw).createFile(any());
        assertTrue(cache.returnCachedDirectories().isEmpty());
    }

    @Test
    void testLoadCachedDirs_readsValidDirectories() throws IOException {
        when(gw.isDirExists(any())).thenReturn(true);

        // Example saved dirs
        when(gw.readTheFile(any())).thenReturn(List.of(
                "/existing/path",
                "/missing/path",
                ""
        ));

        // existing → true, missing → false
        when(gw.isDirExists(Path.of("/existing/path"))).thenReturn(true);
        when(gw.isDirExists(Path.of("/missing/path"))).thenReturn(false);

        cache.loadCachedDirs();

        List<Path> results = cache.returnCachedDirectories();
        assertEquals(1, results.size());
        assertEquals(Path.of("/existing/path"), results.get(0));
    }

    @Test
    void testLoadCachedDirs_handlesIOException() throws IOException {
        when(gw.isDirExists(any())).thenReturn(true);
        when(gw.readTheFile(any())).thenThrow(new IOException("boom"));

        // Should not throw
        assertDoesNotThrow(() -> cache.loadCachedDirs());

        // Should result in empty cache
        assertTrue(cache.returnCachedDirectories().isEmpty());
    }

    // -----------------------------------------------------------------------------------------
    // unLoadCachedDirs()
    // -----------------------------------------------------------------------------------------

    @Test
    void testUnLoadCachedDirs_writesAllCachedDirectories() throws IOException {
        // Populate internal list
        cache.addToCache(Path.of("/a"));
        cache.addToCache(Path.of("/b"));

        when(gw.isFileExists(any())).thenReturn(true);

        cache.unLoadCachedDirs();

        verify(gw).removeFile(any());
        verify(gw).createFile(any());
        verify(gw).appendArowToTheFile(any(), eq("/a"));
        verify(gw).appendArowToTheFile(any(), eq("/b"));
    }

    @Test
    void testUnLoadCachedDirs_handlesIOException() throws IOException {
        when(gw.isFileExists(any())).thenReturn(true);
        doThrow(new IOException("boom")).when(gw).removeFile(any());

        assertDoesNotThrow(() -> cache.unLoadCachedDirs());
    }

    // -----------------------------------------------------------------------------------------
    // addToCache / removeFromCache / returnCachedDirectories
    // -----------------------------------------------------------------------------------------

    @Test
    void testAddToCache() throws IOException {
        cache.addToCache(Path.of("/a"));
        assertEquals(List.of(Path.of("/a")), cache.returnCachedDirectories());
    }

    @Test
    void testRemoveFromCache() throws IOException {
        cache.addToCache(Path.of("/a"));
        cache.addToCache(Path.of("/b"));

        cache.removeFromCache(Path.of("/a"));

        assertEquals(List.of(Path.of("/b")), cache.returnCachedDirectories());
    }
}
