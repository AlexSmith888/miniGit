import own.nio.request.CommandsDispatcher;
import own.nio.utils.CachedDirectories;
import own.nio.utils.InputProcessing;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ResourcesLoader {
    static public void launch () {
        CachedDirectories.loadCachedData();
    }
    static public void finish () {
        CachedDirectories.unLoadCachedData();
    }
}
