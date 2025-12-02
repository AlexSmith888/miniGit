package validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import app.validations.UndoValidation;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UndoValidationtests {
    UndoValidation undo;
    @BeforeEach
    void setUp(){
        undo = new UndoValidation();
    }

    @Test
    @DisplayName("Undo Requires Two Parameters")
    void assertUndoRequiresTwoParametersTest(){
        String[] params = new String[]{"undo"};
        Exception e =
        assertThrows(IllegalArgumentException.class, () -> undo.isValid(params));
        assertEquals(e.getMessage(), "Should be two parameters");
    }

    @Test
    @DisplayName("Should be under miniGit maintenance")
    void assertNotUnderMiniGitControlTest(){
        Path target = Path.of(System.getProperty("user.home")
                + "/" + System.currentTimeMillis());
        String[] params = new String[]{"undo", target.toString()};
        Exception e =
                assertThrows(IllegalArgumentException.class, () -> undo.isValid(params));
        assertEquals(e.getMessage(), "Should be under miniGit control");
    }

}
