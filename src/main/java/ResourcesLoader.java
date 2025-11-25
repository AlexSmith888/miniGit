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
    static public void launch (List<Path> dirs) {
        URL url = ResourcesLoader.class.getResource("/listOfWatchedDirectories.txt");
        try {
            Path p = Paths.get(url.toURI());
            List<String> savedDirs = Files.readAllLines(p);
            for (var dir : savedDirs) {
                if (!Files.exists(Path.of(dir))) {
                    System.out.println("Directory has been removed " +
                            "by a user between application launches");
                }
                if (!dir.isEmpty()) {
                    new CommandsDispatcher().process(
                            new String[]{"track", dir}
                    );
                    dirs.add(Path.of(dir));
                }
            }
            new CachedDirectories(dirs);
        } catch (URISyntaxException e) {
            System.out.println("Failed to load a resource file, URI is incorrect");
        } catch (IOException e) {
            System.out.println("Failed to read a resource file or " +
                    "to track changes for a miniGit directory");
        }
    }
    static public void finish () {
        URL url = ResourcesLoader.class.getResource("/listOfWatchedDirectories.txt");
        try {
            Path p = Paths.get(url.toURI());
            for (var dir : CachedDirectories.returnDirectories()) {
                Files.writeString(p, dir.toString());
                Files.writeString(p, "\n");
            };
        } catch (URISyntaxException e) {
            System.out.println("Failed to write to a resource file, URI is incorrect");
        } catch (IOException e) {
            System.out.println("Failed to write to a resource file");
        }
    }
}
