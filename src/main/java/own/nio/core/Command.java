package own.nio.core;

import java.io.IOException;

public interface Command <T> {
    public void execute(T ... items) throws IOException;
}
