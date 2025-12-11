package utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CLIparserTests {
    CLiParser cl;
    @BeforeEach
    void setUp(){
        cl = new CLiParser();
    }
    @Test
    public void assertReturnedInputSuccessful(){
        String row = "init /folder1/folder2";
        assertArrayEquals(new String[]{"init", "/folder1/folder2"}
                , cl.returnInitInput(row));
        String row1 = " init /folder1/FOLDER2 ";
        assertArrayEquals(new String[]{"init", "/folder1/FOLDER2"}
                , cl.returnInitInput(row1));

        String row2 = " track /FOLDER1/FOLDER2 \"dsdcac asdasd asdasda\"";
        assertArrayEquals(new String[]{"track", "/FOLDER1/FOLDER2"
                        , "\"dsdcac asdasd asdasda\""}
                , cl.returnInitInput(row2));
        String row3 = " track /FOLDER1\"/FOLDER2 \"dsdcac asdasd asdasda\"";
        assertArrayEquals(new String[]{"track", "/FOLDER1\"/FOLDER2"
                        , "\"/FOLDER2 \"dsdcac asdasd asdasda\""}
                , cl.returnInitInput(row3));
    }
}
