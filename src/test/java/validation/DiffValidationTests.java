package validation;

import app.validations.DiffValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class DiffValidationTests {
    DiffValidation diff;
    @BeforeEach
    void setUp(){
        diff = new DiffValidation();
    }
    @Test
    @DisplayName("Requires at least 4 parameters")
    public void assertInsufficientParametersList(){
        String[] arr = new String[]{"diff", "/folder1/folder2", "42df34"};
        Exception e =
                assertThrows(IllegalArgumentException.class, () -> diff.isValid(arr));
        assertEquals("Requires a command, directory, and two short commit names"
                , e.getMessage());
    }

    @Test
    @DisplayName("Throws if Directory not exists")
    public void assertDirectoryNotExists(){
        String[] arr = new String[]{"diff", "/folder1/folder2", "42df34", "42df34"};
        Exception e =
                assertThrows(IllegalArgumentException.class, () -> diff.isValid(arr));
        assertEquals("Should be an existing directory", e.getMessage());
    }

    @Test
    @DisplayName("A directory should exist and be under miniGit maintenance")
    public void asserSucceedsWhenDirectoryExists() throws IOException {
        Path path = Path.of(System.getProperty("user.home")
                + "/" + System.currentTimeMillis());
        try {
            Files.createDirectory(path);
            String[] arr = new String[]{"diff", path.toString(), "a3F5tr67", "a3F5tr67"};
            Exception e =
                    assertThrows(IllegalArgumentException.class, () -> diff.isValid(arr));
            assertEquals("Should be under miniGit control", e.getMessage());
        } catch (IOException e) {}
        finally {
            Files.delete(path);
        }
    }
    @Test
    @DisplayName("Commits should exist")
    public void asserFailsWhenDirectoryDoesNotExist() throws IOException {
        Path path = Path.of(System.getProperty("user.home")
                + "/" + System.currentTimeMillis());
        Path path1 = Path.of(path + "/miniGit/commits");;
        try {
            Files.createDirectory(path);
            Files.createDirectory(path1);
            String[] arr = new String[]{"diff", path.toString(), "a3F5tr67", "a3F5dsd67"};
            Exception e =
                    assertThrows(IllegalArgumentException.class, () -> diff.isValid(arr));
            assertEquals("A commit tree does not have any commits yet", e.getMessage());
        } catch (IOException e) {}
        finally {
            Files.delete(path);
        }
    }
    @Test
    @DisplayName("At least two commits are required")
    public void asserAtLeastTwoCommitsShouldExist() throws IOException {
        Path path = Path.of(System.getProperty("user.home")
                + "/" + System.currentTimeMillis());
        Path path1 = Path.of(path + "/miniGit/commits/a3F5tr67");;
        try {
            Files.createDirectory(path);
            Files.createDirectory(path1);
            String[] arr = new String[]{"diff", path.toString(), "a3F5tr67", "a3F5dsd67"};
            Exception e =
                    assertThrows(IllegalArgumentException.class, () -> diff.isValid(arr));
            assertEquals("Only one commit persists in the commit folder", e.getMessage());
            Exception e1 =
                    assertThrows(IllegalArgumentException.class, () -> diff.isValid(arr));
            assertEquals("Both commits should exist", e.getMessage());
        } catch (IOException e) {}
        finally {
            Files.delete(path);
        }
    }


}
