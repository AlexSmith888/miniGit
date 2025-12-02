package cli;

import infrastructure.cache.CachedCommitTrees;
import infrastructure.cache.CachedDirectories;

public class ResourcesLoader {
    static public void launch () {
        CachedDirectories.loadCachedData();
        CachedCommitTrees.loadCachedData();
    }
    static public void finish () {
        CachedDirectories.unLoadCachedData();
        CachedCommitTrees.unLoadCachedData();
    }
}
