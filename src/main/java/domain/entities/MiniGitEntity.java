package domain.entities;

import java.nio.file.Path;

public interface MiniGitEntity {
    public Path returnSourceDir();
    public Path returnSourceGitDir();
    public Path returnSourceGitTempDir();
    public Path returnSourceGitCommitDir();
    public String [] returnSourceRawRequest();
    public String returnCommand();
}
