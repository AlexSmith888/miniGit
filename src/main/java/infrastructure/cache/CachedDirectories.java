package infrastructure.cache;

import domain.services.RequestsDispatcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class CachedDirectories {
    static List<Path> miniGitDirectories = new ArrayList<>();
    private static Path root = Path.of(System.getProperty("user.home")
            + "/listOfWatchedDirectories");
    private static String file = "/listOfWatchedDirectories.txt";
    private static Path cached = Path.of(root + file);
    static public void loadCachedData(){
        try {
            if (!Files.exists(root)) {
                System.out.println("Directory has been removed " +
                        "by a user between application launches");
                System.out.println("Initializing a resource file on the disk ... ");
                Files.createDirectory(root);
                Files.createFile(cached);
                return;
            }
            List<String> savedDirs = Files.readAllLines(cached);
            for (var dir : savedDirs) {
                if (!Files.exists(Path.of(dir))) {
                    System.out.format("Directory %s has been removed " +
                            "by a user between application launches", dir.toString());
                    System.out.println("\n");
                    continue;
                }
                if (!dir.isEmpty()) {
                    new RequestsDispatcher().process(
                            new String[]{"track", dir}
                    );
                    miniGitDirectories.add(Path.of(dir));
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to read a resource file or " +
                    "to track changes for a miniGit directory");
        }
    }
    static public void unLoadCachedData(){
        try {
            if (Files.exists(cached)) {
                Files.delete(cached);
                Files.createFile(cached);
            }
            for (var dir : returnDirectories()) {
                Files.writeString(cached, dir.toString(), StandardOpenOption.APPEND);
                Files.writeString(cached, "\n", StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            System.out.println("Failed to unload working directories");
        }
    }
    static public List<Path> returnDirectories(){
        return miniGitDirectories;
    }
    static public void removeDirectory(Path dir){
        miniGitDirectories.remove(dir);
    }
}
