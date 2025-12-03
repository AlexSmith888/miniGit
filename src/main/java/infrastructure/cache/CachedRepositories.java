package infrastructure.cache;
import infrastructure.entities.FileSystemGateway;
import infrastructure.entities.RepositoriesGateway;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class CachedRepositories implements RepositoriesGateway {

    List<Path> miniGitDirectories = new ArrayList<>();
    private Path root = Path.of(System.getProperty("user.home")
            + "/listOfWatchedDirectories");
    private String file = "/listOfWatchedDirectories.txt";
    private Path cached = Path.of(root + file);
    private FileSystemGateway gw;

    public CachedRepositories(FileSystemGateway gw) {
        this.gw = gw;
    }

    @Override
    public void loadCachedDirs() {
        try {
            if (!gw.isDirExists(root)) {
                System.out.println("Directory has been removed " +
                        "by a user between application launches");
                System.out.println("Initializing a resource file on the disk ... ");
                gw.createDir(root);
                gw.createFile(cached);
                return;
            }
            List<String> savedDirs = gw.readTheFile(cached);
            for (var dir : savedDirs) {
                if (!gw.isDirExists(Path.of(dir))) {
                    System.out.format("Directory %s has been removed " +
                            "by a user between application launches", dir.toString());
                    System.out.println("\n");
                    continue;
                }
                if (!dir.isEmpty()) {
                    miniGitDirectories.add(Path.of(dir));
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to read a resource file or " +
                    "to track changes for a miniGit directory");;
        }
    }

    @Override
    public void unLoadCachedDirs() {
        try {
            if (gw.isFileExists(cached)) {
                gw.removeFile(cached);
                gw.createFile(cached);
            }
            for (var dir : returnCachedDirectories()) {
                gw.appendArowToTheFile(cached, dir.toString());
            }
        } catch (IOException e) {
            System.out.println("Failed to unload working directories");
        }
    }

    @Override
    public List<Path> returnCachedDirectories() {
        return miniGitDirectories;
    }

    @Override
    public void removeFromCache(Path dir) throws IOException {
        miniGitDirectories.remove(dir);
    }
}
