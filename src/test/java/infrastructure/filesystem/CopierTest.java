package infrastructure.filesystem;

import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static org.junit.jupiter.api.Assertions.*;

class CopierTest {

    private Path source;
    private Path target;
    private Copier copier;

    @BeforeEach
    void setup() throws IOException {
        source = Files.createTempDirectory("copier-src");
        target = Files.createTempDirectory("copier-target");
        copier = new Copier();
        copier.setSource(source);
        copier.setTarget(target);
    }

    @AfterEach
    void cleanup() throws IOException {
        deleteRecursively(source);
        deleteRecursively(target);
    }

    private void deleteRecursively(Path root) throws IOException {
        if (!Files.exists(root)) return;
        Files.walk(root)
                .sorted((a, b) -> b.compareTo(a))
                .forEach(p -> {
                    try { Files.deleteIfExists(p); } catch (IOException ignored) {}
                });
    }

    // -------------------------------------------------------------------------
    // BASIC COPY
    // -------------------------------------------------------------------------

    @Test
    void copies_single_file() throws IOException {
        Path file = Files.createFile(source.resolve("a.txt"));
        Files.writeString(file, "hello");

        Files.walkFileTree(source, copier);

        Path copied = target.resolve("a.txt");
        assertTrue(Files.exists(copied));
        assertEquals("hello", Files.readString(copied));
    }

    @Test
    void copies_nested_directories_and_files() throws IOException {
        Path dir = Files.createDirectories(source.resolve("sub/inner"));
        Path file = Files.createFile(dir.resolve("b.txt"));
        Files.writeString(file, "data");

        Files.walkFileTree(source, copier);

        Path copiedDir = target.resolve("sub/inner");
        Path copiedFile = copiedDir.resolve("b.txt");

        assertTrue(Files.isDirectory(copiedDir));
        assertTrue(Files.exists(copiedFile));
        assertEquals("data", Files.readString(copiedFile));
    }

    // -------------------------------------------------------------------------
    // EXCLUDED PATHS
    // -------------------------------------------------------------------------

    @Test
    void excluded_directory_is_not_copied() throws IOException {
        Path excludedDir = Files.createDirectories(source.resolve("excluded"));
        Files.createFile(excludedDir.resolve("skip.txt"));

        copier.addToExcludedList(excludedDir);

        Files.walkFileTree(source, copier);

        assertFalse(Files.exists(target.resolve("excluded")));
    }

    @Test
    void truncateExcludedList_removes_all_exclusions() throws IOException {
        Path excludedDir = Files.createDirectories(source.resolve("excluded"));
        Files.createFile(excludedDir.resolve("file.txt"));

        copier.addToExcludedList(excludedDir);
        copier.truncateExcludedList();

        Files.walkFileTree(source, copier);

        assertTrue(Files.exists(target.resolve("excluded/file.txt")));
    }

    // -------------------------------------------------------------------------
    // ROOT DIRECTORY HANDLING
    // -------------------------------------------------------------------------

    @Test
    void root_directory_is_not_created_inside_target() throws IOException {
        Files.walkFileTree(source, copier);

        // target should not contain nested source directory
        assertFalse(Files.exists(target.resolve(source.getFileName())));
    }

    // -------------------------------------------------------------------------
    // FAILURE BEHAVIOR
    // -------------------------------------------------------------------------

    @Test
    void visitFile_returns_terminate_on_copy_failure() throws IOException {
        // create directory where file copy will fail
        Path file = Files.createFile(source.resolve("fail.txt"));
        Files.writeString(file, "x");

        // make target read-only to provoke failure
        target.toFile().setReadOnly();

        FileVisitResult result = copier.visitFile(file, Files.readAttributes(file, BasicFileAttributes.class));

        assertEquals(FileVisitResult.TERMINATE, result);

        // restore permissions for cleanup
        target.toFile().setWritable(true);
    }

    @Test
    void visitFileFailed_throws_exception() {
        IOException ex = new IOException("boom");

        assertThrows(IOException.class,
                () -> copier.visitFileFailed(source.resolve("x"), ex));
    }

    @Test
    void postVisitDirectory_throws_exception_on_error() {
        IOException ex = new IOException("boom");

        assertThrows(IOException.class,
                () -> copier.postVisitDirectory(source, ex));
    }
}
