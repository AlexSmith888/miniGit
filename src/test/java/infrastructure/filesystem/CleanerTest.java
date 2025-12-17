package infrastructure.filesystem;

import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static org.junit.jupiter.api.Assertions.*;

class CleanerTest {

    private Path root;
    private Cleaner cleaner;

    @BeforeEach
    void setup() throws IOException {
        root = Files.createTempDirectory("cleaner-root");
        cleaner = new Cleaner();
        cleaner.setSource(root);
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
    // BASIC CLEANUP
    // -------------------------------------------------------------------------

    @Test
    void deletes_all_files_and_subdirectories_except_root() throws IOException {
        Path sub = Files.createDirectories(root.resolve("sub"));
        Path file = Files.createFile(sub.resolve("file.txt"));
        Files.writeString(file, "data");

        Files.walkFileTree(root, cleaner);

        // root remains
        assertTrue(Files.exists(root));

        // subdir and file removed
        assertFalse(Files.exists(sub));
        assertFalse(Files.exists(file));
    }

    // -------------------------------------------------------------------------
    // EXCLUDED PATHS
    // -------------------------------------------------------------------------

    @Test
    void excluded_directory_is_not_deleted() throws IOException {
        Path keep = Files.createDirectories(root.resolve("keep"));
        Path keepFile = Files.createFile(keep.resolve("safe.txt"));
        Files.writeString(keepFile, "safe");

        cleaner.addToExcludedList(keep);

        Files.walkFileTree(root, cleaner);

        assertTrue(Files.exists(keep));
        assertTrue(Files.exists(keepFile));
    }

    @Test
    void truncateExcludedList_allows_deletion_after_reset() throws IOException {
        Path dir = Files.createDirectories(root.resolve("dir"));
        Files.createFile(dir.resolve("file.txt"));

        cleaner.addToExcludedList(dir);
        cleaner.truncateExcludedList();

        Files.walkFileTree(root, cleaner);

        assertFalse(Files.exists(dir));
    }

    // -------------------------------------------------------------------------
    // ROOT HANDLING
    // -------------------------------------------------------------------------

    @Test
    void root_directory_is_never_deleted() throws IOException {
        Files.walkFileTree(root, cleaner);
        assertTrue(Files.exists(root));
    }

    // -------------------------------------------------------------------------
    // FAILURE PATHS
    // -------------------------------------------------------------------------

    @Test
    void visitFileFailed_throws_exception() {
        IOException ex = new IOException("boom");

        assertThrows(IOException.class,
                () -> cleaner.visitFileFailed(root.resolve("x"), ex));
    }

    @Test
    void postVisitDirectory_throws_exception_on_error() {
        IOException ex = new IOException("boom");

        assertThrows(IOException.class,
                () -> cleaner.postVisitDirectory(root, ex));
    }

    // -------------------------------------------------------------------------
    // EDGE CASES
    // -------------------------------------------------------------------------

    @Test
    void deleting_empty_directory_tree_does_not_fail() throws IOException {
        Files.walkFileTree(root, cleaner);
        assertTrue(Files.exists(root));
    }

    @Test
    void multiple_nested_directories_are_deleted_in_correct_order() throws IOException {
        Path nested = Files.createDirectories(root.resolve("a/b/c"));
        Files.createFile(nested.resolve("x.txt"));

        Files.walkFileTree(root, cleaner);

        assertFalse(Files.exists(root.resolve("a")));
    }
}