package validation;

import app.validations.CommitValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommitValidationTests {
    CommitValidation commit;

    @BeforeEach
    void setUp() {
        commit = new CommitValidation();
    }

    @Test
    @DisplayName("Requires 3 parameters as an input")
    public void asserFailsWhenLessThanThreeParameters(){
        String[] arr = new String[]{"commit", "/folder1/folder2"};
        Exception e =
        assertThrows(IllegalArgumentException.class, () -> commit.isValid(arr));
        assertEquals("Requires a {commit}{path}{\"message - either blank or not\"}}"
                , e.getMessage());
    }

    @Test
    @DisplayName("Should be an existing directory")
    public void asserFailsWhenRepositoryNotAMiniGitRepository(){
        Path path = Path.of(System.getProperty("user.home") + "/" + System.currentTimeMillis());
        String[] arr = new String[]{"commit", path.toString(), "initial commit"};
        Exception e =
                assertThrows(IllegalArgumentException.class, () -> commit.isValid(arr));
        assertEquals("Should be an existing directory"
                , e.getMessage());
    }
    @Test
    @DisplayName("Should be under miniGit maintenance")
    public void asserFailsNotUnderMiniGitMaintenance() throws IOException {
        Path path = Path.of(System.getProperty("user.home") + "/" + System.currentTimeMillis());;
        try {
            Files.createDirectory(path);
            String[] arr = new String[]{"commit", path.toString(), "initial commit"};
            Exception e =
                    assertThrows(IllegalArgumentException.class, () -> commit.isValid(arr));
            assertEquals("Should be under miniGit control"
                    , e.getMessage());
        } catch (IOException e) {}
        finally {
            Files.delete(path);
        }
    }
}
