package app.state;

import infrastructure.entities.CommitsCacheGateway;
import infrastructure.entities.FileSystemGateway;
import infrastructure.entities.RepositoriesGateway;
import infrastructure.filesystem.Cleaner;
import infrastructure.filesystem.Copier;
import infrastructure.filesystem.Eraser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class StateManager implements AppState{
    private Path root = Path.of(System.getProperty("user.home"));
    HashMap<String, String> commits = new HashMap<>();
    HashMap<String, String> repoMatcher = new HashMap<>();
    List<Path> repositories;
    CommitsCacheGateway commitGw;
    RepositoriesGateway repoGw;
    FileSystemGateway fs;
    Eraser eraser;
    Copier copier;
    Cleaner cleaner;

    public StateManager(
            CommitsCacheGateway commitGw
            , RepositoriesGateway repoGw
            , FileSystemGateway fs
            , Eraser er
            , Copier cp
            , Cleaner cl) {
        this.commitGw = commitGw;
        this.repoGw = repoGw;
        this.fs = fs;
        this.eraser = er;
        this.copier = cp;
        this.cleaner = cl;
        this.root = Path.of(root + "/" + System.currentTimeMillis());
    }

    @Override
    public void saveCurrentState() throws IOException {
        saveRepositories();
        saveCommits();
    }

    @Override
    public void recoverPreviousState() throws IOException {
        recoverCommits();
        recoverRepositories();
        fs.deleteRecursively(root, eraser);
    }
    private void saveRepositories() throws IOException {
        System.out.println("Creating a directory to transfer repositories : "
                +  root.toString());
        fs.createDir(root);
        repositories = new ArrayList<>(repoGw.returnCachedDirectories());
        for (var path : repositories) {
            Path target = Path.of(root + "/" + path.getFileName()
                    + "_" + System.currentTimeMillis());
            repoMatcher.put(path.toString(), target.toString());
            fs.createDir(target);
            copier.setSource(path);
            copier.setTarget(target);
            fs.copyRecursively(path, copier);
            System.out.format("Copying %s to %s", path, target);
            System.out.println("\n");
        }
    }
    private void recoverRepositories() throws IOException {
        for (var current : repoMatcher.entrySet()) {
            Path target = Path.of(current.getKey());
            System.out.println("Cleaning target directories : " + target);
            cleaner.addToExcludedList(target);
            fs.deleteRecursively(target, cleaner);
        }
        List<String> targets = new ArrayList<>(repoMatcher.keySet());
        for (var current : targets) {
            Path source = Path.of(repoMatcher.get(current));
            Path target = Path.of(current);
            copier.setSource(source);
            copier.setTarget(target);
            System.out.println("Copying data from backup directory into the main : "
                    + source + " ----> " + target);
            fs.copyRecursively(source, copier);
        }

        for (var val : repoGw.returnCachedDirectories()) {
            System.out.println("removing recent cached directories");
            repoGw.removeFromCache(val);
        }
        for (var val : repositories) {
            System.out.println("adding saved directories");
            repoGw.addToCache(val);
        }
    }
    private void saveCommits() throws IOException {
        System.out.println("Saving commits in memory ... ");
        for (var parent : repositories) {
            commits.putAll(commitGw.retrieveSubtree(parent.toString()));
        }
    }
    private void recoverCommits() throws IOException {
        List<Path> current = repoGw.returnCachedDirectories();
        for (var parent : current) {
            System.out.println("removing commits based on their parent commit from memory");
            commitGw.removeCommitsTree(parent);
        }
        for (var parent : repositories) {
            System.out.println("adding commits to the tree based on their saved parental commits");
            String par = parent.toString();
            Queue<String> queue = new LinkedList<>();
            queue.add(par);
            while (!queue.isEmpty()) {
                String curr = queue.poll();
                if (curr.isEmpty()) {
                    continue;
                }
                commitGw.addCommitToTree(parent, curr);
                String next = commits.get(curr);
                queue.add(next);
            }
        }
    }
}
