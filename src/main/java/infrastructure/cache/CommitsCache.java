package infrastructure.cache;

import infrastructure.entities.CommitsCacheLoader;
import infrastructure.entities.FileSystemGateway;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;

public class CommitsCache implements CommitsCacheLoader {

    private HashMap<String, String> commits = new HashMap<>();
    private Path commitsDir = Path.of(System.getProperty("user.home")
            + "/commitsTree");
    private String commitsFile = "/commits.txt";
    private Path commitsPath = Path.of(commitsDir + commitsFile);
    FileSystemGateway fsGateway;

    public CommitsCache(FileSystemGateway gateway) {
        this.fsGateway = gateway;
    }

    @Override
    public void loadInMemory() throws IOException{
        try {
            if (!fsGateway.isDirExists(commitsDir)) {
                System.out.println("Creating a commits' file on the disk ... ");
                fsGateway.createDir(commitsDir);
                fsGateway.createFile(commitsPath);
                return;
            }
            List<String> savedCommits = Files.readAllLines(commitsPath);
            for (var commitLine : savedCommits) {
                if (!commitLine.isEmpty()) {
                    String[] arr = commitLine.split(" ");
                    if (arr.length == 1) {
                        commits.put(arr[0], "");
                        continue;
                    }
                    commits.put(arr[0], arr[1]);
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to read commits' file");
            throw e;
        }
    }

    @Override
    public void flushToTheDisk() throws IOException {
        try {
            if (fsGateway.isFileExists(commitsPath)) {
                fsGateway.removeFile(commitsPath);
                fsGateway.createFile(commitsPath);
            }
            for (var entry : returnCurrentState().entrySet()) {
                fsGateway.appendArowToTheFile(commitsPath, entry.getKey() + " " + entry.getValue());
            }
        } catch (IOException e) {
            System.out.println("Failed to flush commits' data to the disk .. ");
            throw e;
        }
    }

    public void setCommitsPath(Path path) {
        this.commitsPath = path;
    }

    @Override
    public HashMap<String, String> returnCurrentState() {
        return commits;
    }
}
