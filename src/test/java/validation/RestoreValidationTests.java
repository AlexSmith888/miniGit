package validation;

import app.validations.RestoreValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class RestoreValidationTests {
    RestoreValidation val;
    @BeforeEach
    void setUp(){
        val = new RestoreValidation();
    }

    @Test
    @DisplayName("At least three parameters are required")
    public void assertInsufficientParametersLit(){
        Path path = Path.of(System.getProperty("user.home") + "/" + System.currentTimeMillis());
        String[] arr = new String[]{"restore", path.toString()};
        Exception e =
                assertThrows(IllegalArgumentException.class, () -> val.isValid(arr));
        assertEquals("Insufficient parameters list"
                , e.getMessage());
    }

    @Test
    @DisplayName("A commit folder should exist on the disk")
    public void asserSucceedsWhenDirectoryExists() throws IOException {
        Path path = Path.of(System.getProperty("user.home")
                + "/" + System.currentTimeMillis());
        Path path1 = Path.of(path + "/miniGit/commits/a3F5tr67");;
        try {
            Files.createDirectory(path);
            Files.createDirectory(path1);
            String[] arr = new String[]{"restore", path.toString(), "a3F5tr67"};
                    assertDoesNotThrow(() -> val.isValid(arr));
        } catch (IOException e) {}
        finally {
            Files.delete(path);
        }
    }

    @Test
    @DisplayName("Throws if not exist")
    public void asserFailsWhenDirectoryDoesNotExist() throws IOException {
        Path path = Path.of(System.getProperty("user.home")
                + "/" + System.currentTimeMillis());
        Path path1 = Path.of(path + "/miniGit/commits");;
        try {
            Files.createDirectory(path);
            Files.createDirectory(path1);
            String[] arr = new String[]{"restore", path.toString(), "a3F5tr67"};
            Exception e =
            assertThrows(IllegalArgumentException.class, () -> val.isValid(arr));
            assertEquals("Commit does not exist", e.getMessage());
        } catch (IOException e) {}
        finally {
            Files.delete(path);
        }
    }
}
