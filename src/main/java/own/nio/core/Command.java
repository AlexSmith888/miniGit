package own.nio.core;

import java.io.IOException;

public interface Command {
    public void execute(MIniGitClass entity) throws IOException;
}
