package infrastructure.entities;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface RepositoriesGateway {
    public void loadCachedDirs();
    public void unLoadCachedDirs();

    public List<Path> returnCachedDirectories();
    public void removeFromCache(Path dir) throws IOException;
    void addToCache(Path dir) throws IOException;
}
