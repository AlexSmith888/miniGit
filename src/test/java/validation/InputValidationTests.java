package validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import own.nio.core.Commands;
import own.nio.validation.InputValidation;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InputValidationTests {
    InputValidation instance;
    @BeforeEach
    void setUp(){
        instance = new InputValidation();
    }

    @Test
    @DisplayName("At least one parameter should be provided")
    void assertShouldHaveParameters(){
        Exception e =
        assertThrows(IllegalArgumentException.class, () -> instance.isValid(new String[]{}));
        assertEquals(e.getMessage(), "An empty command");
    }

    @Test
    @DisplayName("All the commands are listed in a dedicated ENUM type")
    void assertCommandExistInEnum(){
        for (var command : Commands.values()) {
            assertDoesNotThrow(() -> instance.isValid(new String[]{command.get()}));
        }
    }

    @Test
    void assertCommandDoesNotExistInEnum(){
        List<String> wrongCommands = List.of("exit", "change", "cd", "finish");

        for (var command : wrongCommands) {
            assertAll(
                    () -> {
                        Exception e = assertThrows(IllegalArgumentException.class
                                , () -> instance.isValid(new String[]{command}));
                        assertEquals(e.getMessage()
                                , "Only supported commands are allowed");
                    }
            );
        }
    }
}
