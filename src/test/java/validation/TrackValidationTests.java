package validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import own.nio.validation.TrackValidation;

import java.nio.file.Path;
import java.security.spec.ECField;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TrackValidationTests {
    TrackValidation instance ;
    @BeforeEach
    void setUp(){
        instance = new TrackValidation();
    }

    @Test
    @DisplayName("Both parameters should be present")
    void assertFailsIfParametersMissedTest(){
        String[] params = new String[]{"track"};
        Exception e =
        assertThrows(IllegalArgumentException.class, () -> instance.isValid(params));
        assertEquals(e.getMessage(), "Insufficient parameters list");
    }

    @Test
    @DisplayName("init precedes any other command")
    void assertTrackSucceedsInitTest(){
        Path test = Path.of(System.getProperty("user.home")
                + "/" + "_" + System.currentTimeMillis());
        String[] params = new String[]{"track", "test"};
        Exception e =
                assertThrows(IllegalArgumentException.class, () -> instance.isValid(params));
        assertEquals(e.getMessage(), "Should use miniGit {init} first" +
                ", skipping further processing");
    }
}
