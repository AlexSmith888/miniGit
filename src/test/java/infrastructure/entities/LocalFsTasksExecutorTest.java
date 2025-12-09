package infrastructure.entities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LocalFsTasksExecutorTest {
    private static final Logger logger = (Logger) LogManager.getLogger(LocalFsTasksExecutor.class);
    String root = System.getProperty("user.home");
    LocalFsTasksExecutor instance;
    Path Directory1;
    Path Directory2;
    String File1;
    String File2;

    @BeforeEach
    void setUp(){
        long time = System.currentTimeMillis();
        instance = new LocalFsTasksExecutor(logger);
        Directory1 = Path.of(root + "/temporary1" + time);
        Directory2 = Path.of(root + "/temporary2" + time);
        File1 = "test1.txt";
        File2 = "test2.txt";
    }

    @Test
    @DisplayName("Creates a directory if does not exists")
    public void createDirTest() throws IOException {
        instance.createDir(Directory1);
        assertTrue(Files.exists(Directory1));
    }

    @Test
    @DisplayName("Do nothing if a target directory exists")
    public void createDirIfExistsTest() throws IOException {
        instance.createDir(Directory1);
        assertDoesNotThrow(() -> instance.createDir(Directory1));
    }

    @Test
    @DisplayName("Removes an empty directory if exist")
    public void removeDirTest() throws IOException {
        Files.createDirectory(Directory1);
        instance.removeDir(Directory1);
        assertFalse(Files.exists(Directory1));
    }

    @Test
    @DisplayName("Skips none-empty directory")
    public void removeDoesNothingDirTest() throws IOException {
        Files.createDirectory(Directory1);
        Files.createFile(Path.of(Directory1 + "/" + File1));
        instance.removeDir(Directory1);
        assertTrue(Files.exists(Directory1));
    }

    @Test
    @DisplayName("Copies an empty directory if exist")
    public void copyDirTest() throws IOException {
        Files.createDirectory(Directory1);
        Files.createDirectory(Directory2);
        instance.copyDir(Directory1, Directory2);
        assertTrue(Files.exists(Path.of(Directory2 + "/" + Directory1.getFileName())));
    }

    @Test
    @DisplayName("Copies only if a target does not exist")
    public void copyReplacesDirTest() throws IOException {
        Files.createDirectory(Directory1);
        Files.createDirectory(Directory2);

        Files.createFile(Path.of(Directory2 + "/"+ File2));
        Files.createDirectory(Path.of(Directory1 + "/" + Directory2.getFileName()));
        Files.createFile(Path.of(Directory1 + "/"
                + Directory2.getFileName() + "/" + File1));

        instance.copyDir(Directory2, Directory1);
        assertAll(
                ()->{
                    List<Path> arr = Files.list(Path.of(Directory1 + "/" + Directory2.getFileName())).toList();
                    assertTrue(arr.contains(
                            Path.of(Directory1 + "/"
                                    + Directory2.getFileName() + "/" + File1)
                    ));
                }
        );
    }

    @Test
    @DisplayName("Creates a file if not exists")
    public void createFileTest() throws IOException {
        Files.createDirectory(Directory1);
        instance.createFile(Path.of(Directory1 + "/" + File1));
        assertTrue(Files.exists(Path.of(Directory1 + "/" + File1)));
    }

    @Test
    @DisplayName("Skips if a file exists")
    public void createFileSkipsTest() throws IOException {
        Files.createDirectory(Directory1);
        instance.createFile(Path.of(Directory1 + "/" + File1));
        assertDoesNotThrow(() -> instance.createFile(Path.of(Directory1 + "/" + File1)));
        assertTrue(Files.exists(Path.of(Directory1 + "/" + File1)));
    }

    @Test
    @DisplayName("Removes a file if exists")
    public void removeFileTest() throws IOException {
        Files.createDirectory(Directory1);
        Files.createFile(Path.of(Directory1 + "/" + File1));
        instance.removeFile(Path.of(Directory1 + "/" + File1));
        assertFalse(Files.exists(Path.of(Directory1 + "/" + File1)));
    }

    @Test
    @DisplayName("Copies a file")
    public void copyFileTest() throws IOException {
        Files.createDirectory(Directory1);
        Files.createDirectory(Directory2);
        Path file = Path.of(Directory1 + "/" + File1);
        Files.createFile(file);

        instance.copyFile(file, Directory2);
        assertTrue(Files.exists(Path.of(Directory2 + "/" + File1)));
    }

    @Test
    @DisplayName("Replaces an existing file")
    public void copyFileIfExistsTest() throws IOException {
        Files.createDirectory(Directory1);
        Files.createDirectory(Directory2);
        Path file = Path.of(Directory1 + "/" + File1);
        Path file1 = Path.of(Directory2 + "/" + File1);
        Files.createFile(file);
        Files.createFile(file1);

        instance.copyFile(file, Directory2);
        assertTrue(Files.exists(Path.of(Directory2 + "/" + File1)));
    }

    @AfterEach
    void cleanUp() throws IOException {
        Files.walkFileTree(Directory1, new DirectoriesCleaner());
        Files.walkFileTree(Directory2, new DirectoriesCleaner());
    }
}
