package validation;

import app.validations.HistoryValidation;
import org.apache.logging.log4j.core.config.plugins.convert.HexConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HistoryValidationTests {
    HistoryValidation hist;
    @BeforeEach
    public void setUp(){
        hist = new HistoryValidation();
    }

    @Test
    @DisplayName("Requires at least two parameters")
    public void assertFails(){
        String[] values = new String[]{"history"};
        Exception e =
        assertThrows(IllegalArgumentException.class, () -> hist.isValid(values));
        assertEquals("Requires a second parameter. Choose a directory to be watched", e.getMessage());
    }

    @Test
    @DisplayName("Should be an existing directory")
    public void asserFailsWhenRepositoryNotAMiniGitRepository(){
        Path path = Path.of(System.getProperty("user.home") + "/" + System.currentTimeMillis());
        String[] arr = new String[]{"history", path.toString()};
        Exception e =
                assertThrows(IllegalArgumentException.class, () -> hist.isValid(arr));
        assertEquals("Should be an existing directory"
                , e.getMessage());
    }
    @Test
    @DisplayName("Should be under miniGit maintenance")
    public void asserFailsNotUnderMiniGitMaintenance() throws IOException {
        Path path = Path.of(System.getProperty("user.home") + "/" + System.currentTimeMillis());;
        try {
            Files.createDirectory(path);
            String[] arr = new String[]{"history", path.toString()};
            Exception e =
                    assertThrows(IllegalArgumentException.class, () -> hist.isValid(arr));
            assertEquals("Should be under miniGit control"
                    , e.getMessage());
        } catch (IOException e) {}
        finally {
            Files.delete(path);
        }
    }
    @Test
    @DisplayName("Commits folder should be non empty")
    public void asserFailsWhenDirectoryISEmpty() throws IOException {
        Path path = Path.of(System.getProperty("user.home") + "/" + System.currentTimeMillis());
        Path path1 = Path.of(path + "/miniGit/commits");;
        try {
            Files.createDirectory(path);
            Files.createDirectory(path1);
            String[] arr = new String[]{"history", path.toString()};
            Exception e =
                    assertThrows(IllegalArgumentException.class, () -> hist.isValid(arr));
            assertEquals("A commit tree does not have any commits yet"
                    , e.getMessage());
        } catch (IOException e) {}
        finally {
            Files.delete(path);
        }
    }
}
