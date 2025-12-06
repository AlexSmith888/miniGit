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
    private HashMap<String, String> commits;
    private Path root;
    private final CommitsCacheGateway commitGw;
    private final FileSystemGateway fs;
    private final Eraser eraser;
    private final Copier copier;
    private final Cleaner cleaner;

    public StateManager(CommitsCacheGateway commitGw, FileSystemGateway fs,
                        Eraser eraser,
                        Copier copier,
                        Cleaner cleaner) {
        this.commitGw = commitGw;
        this.fs = fs;
        this.eraser = eraser;
        this.copier = copier;
        this.cleaner = cleaner;
    }
    public void setRoot(Path source){
        this.root = source;
    }
    public Path getRoot() {
        return root;
    }
    private Path getRootBackup() {
        return Path.of(root.getParent() + "/" + System.currentTimeMillis());
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
        fs.deleteRecursively(getRootBackup(), eraser);
    }
    public void clean() throws IOException {
        System.out.println("here");
        fs.deleteRecursively(getRootBackup(), eraser);
    }
    private void saveRepositories() throws IOException {
        fs.createDir(getRootBackup());
        copier.setSource(getRoot());
        copier.setTarget(getRootBackup());
        fs.copyRecursively(getRoot(), copier);

        System.out.format("Copying %s to %s", getRoot(), getRootBackup());
        System.out.println("\n");
    }
    private void recoverRepositories() throws IOException {
        cleaner.setSource(getRoot());
        fs.deleteRecursively(getRoot(), cleaner);

        copier.setSource(getRootBackup());
        copier.setTarget(getRoot());
        fs.copyRecursively(getRootBackup(), copier);

        System.out.format("Copying %s to %s", getRoot(), getRootBackup());
        System.out.println("\n");
    }
    private void saveCommits() throws IOException {
        System.out.println("Storing all commits in memory");
        commits = new HashMap<>(commitGw.retrieveSubtree(getRoot().toString()));
    }
    private void recoverCommits() throws IOException {
        if (commits.isEmpty()) {
            return;
        }
        commitGw.removeCommitsTree(getRoot());
        //!!! Wrong - use files, both for commits and dirs
        /*String par = getRoot().toString();
        Queue<String> queue = new LinkedList<>();
        queue.add(par);
        while (!queue.isEmpty()) {
            String curr = queue.poll();
            if (curr.isEmpty()) {
                return;
            }
            commitGw.addCommitToTree(getRoot(), curr);
            String next = commits.get(curr);
            queue.add(next);
        }*/
    }
}
