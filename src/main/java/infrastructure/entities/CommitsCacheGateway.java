package infrastructure.entities;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

public interface CommitsCacheGateway {
    void removeCommitsTree(Path dir);

    void removeCommitsSubTree(String commit, Path commitsTree, String meta) throws IOException;

    String getLastCommitForParent(Path dir);

    boolean isCommitExists(Path dir);

    void addCommitToTree(Path dir, String value);

    public HashMap<String, String> retrieveSubtree(String dir);
}
