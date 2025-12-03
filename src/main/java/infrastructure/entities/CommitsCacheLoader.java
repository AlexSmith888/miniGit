package infrastructure.entities;

import java.io.IOException;
import java.util.HashMap;

public interface CommitsCacheLoader {
    public void loadInMemory() throws IOException;
    public void flushToTheDisk() throws IOException;
    public HashMap<String, String> returnCurrentState();
}
