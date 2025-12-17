package infrastructure.filesystem;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests current behavior of Eraser.
 * These tests intentionally reveal design flaws.
 */
class EraserTest {

    private Path root;
    private Eraser eraser;

    @BeforeEach
    void setup() throws IOException {
        root = Files.createTempDirectory("eraser-root");
        eraser = new Eraser();
        eraser.setSource(root);
    }

    @AfterEach
    void cleanup() throws IOException {
        if (Files.exists(root)) {
            Files.walk(root)
                    .sorted((a, b) -> b.compareTo(a))
                    .forEach(p -> {
                        try { Files.deleteIfExists(p); } catch (IOException ignored) {}
                    });
        }
    }

    // -------------------------------------------------------------------------
    // ACTUAL BEHAVIOR TESTS
    // -------------------------------------------------------------------------

    @Test
    void erases_all_files_and_directories_including_root() throws IOException {
        Path sub = Files.createDirectories(root.resolve("sub"));
        Files.createFile(sub.resolve("file.txt"));

        Files.walkFileTree(root, eraser);

        // Root is deleted — this is CURRENT behavior
        assertFalse(Files.exists(root),
                "Eraser deletes the root directory (dangerous behavior)");
    }

    @Test
    void eraser_ignores_excluded_list() throws IOException {
        Path keep = Files.createDirectories(root.resolve("keep"));
        Files.createFile(keep.resolve("safe.txt"));

        eraser.addToExcludedList(keep); // no-op

        Files.walkFileTree(root, eraser);

        assertFalse(Files.exists(keep),
                "Excluded directories are ignored by current implementation");
    }

    // -------------------------------------------------------------------------
    // FAILURE PATHS
    // -------------------------------------------------------------------------

    @Test
    void visitFileFailed_throws_exception() {
        IOException ex = new IOException("boom");

        assertThrows(IOException.class,
                () -> eraser.visitFileFailed(root.resolve("x"), ex));
    }

    @Test
    void postVisitDirectory_throws_exception_on_error() {
        IOException ex = new IOException("boom");

        assertThrows(IOException.class,
                () -> eraser.postVisitDirectory(root, ex));
    }
}