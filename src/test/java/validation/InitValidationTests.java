package validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import app.validations.InitValidation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class InitValidationTests {
    InitValidation instance;
    @BeforeEach
    void setUp(){
        instance = new InitValidation();
    }

    @Test
    @DisplayName("Fails when a directory name has not been provided")
    void assertFailsWhenLessThenTwoParametersTest(){
        String[] arr = new String[]{"init"};
        Exception e = assertThrows(IllegalArgumentException.class, ()-> instance.isValid(arr));
        assertEquals(e.getMessage(), "Requires a second parameter. " +
                "Choose a directory to be watched");
    }

    @Test
    @DisplayName("Fails when a directory does not exist")
    void assertFailsWhenDirectoryDoesNotExistTest(){
        long random = System.currentTimeMillis();
        Path notExist = Path.of("/" + " " + random);
        String[] arr = new String[]{"init", notExist.toString()};

        Exception e = assertThrows(IllegalArgumentException.class, ()-> instance.isValid(arr));
        assertEquals(e.getMessage(), "Should be an existing directory");
    }

    @Test
    @DisplayName("Fails when a miniGit directory exists")
    void assertFailsMiniGitDirectoryExistsTest(){
        long random = System.currentTimeMillis();
        Path root = Path.of(System.getProperty("user.home") + "/" + random);
        Path miniCreated = Path.of(root + "/" + "miniGit");

        try {
            Files.createDirectory(root);
            Files.createDirectory(miniCreated);
        } catch (IOException e) {
            System.out.println("Failed to crete working directories");
            e.printStackTrace();
            return;
        }

        String[] arr = new String[]{"init", root.toString()};
        Exception e = assertThrows(IllegalArgumentException.class, ()-> instance.isValid(arr));
        assertEquals(e.getMessage(), "The directory is Already under the miniGit maintenance");

        try {
            Files.delete(miniCreated);
            Files.delete(root);
        } catch (IOException e1) {
            System.out.println("Failed to delete working directories");
            e1.printStackTrace();
        }
    }
}
