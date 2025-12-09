package app.state;

import domain.entities.MIniGitRepository;
import domain.entities.MiniGitEntity;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class StateManager implements RequestState {
    private HashMap<String, String> commits;
    private List<Path> dirs;


    @Override
    public void saveCurrentState(MIniGitRepository gitRepo) throws IOException {
        gitRepo.returnFileSystem().createDir(gitRepo.returnBackupDirectory());
        gitRepo.returnLogger().info("Saving current state : {} created", gitRepo.returnBackupDirectory());
        gitRepo.returnFileSystem().createDir(gitRepo.returnBackupDataDirectory());
        gitRepo.returnLogger().info("Saving current state : {} created", gitRepo.returnBackupDataDirectory());
        gitRepo.returnCopier().setSource(gitRepo.returnSourceDir());
        gitRepo.returnCopier().setTarget(gitRepo.returnBackupDataDirectory());
        gitRepo.returnFileSystem().copyRecursively(gitRepo.returnSourceDir(), gitRepo.returnCopier());
        gitRepo.returnLogger().info("Saving current state : {} copied to {}"
                , gitRepo.returnSourceDir(), gitRepo.returnBackupDataDirectory());

        commits =
                new HashMap<>(gitRepo.returnCommitsCache().retrieveSubtree(gitRepo.returnSourceDir().toString()));
        dirs =
                new ArrayList<>(gitRepo.returnRepos().returnCachedDirectories());

        gitRepo.returnLogger().info("Saving current state : internal app caches saved");

        gitRepo.returnFileSystem().createFile(gitRepo.returnBackupCommitsFile());
        gitRepo.returnFileSystem().createFile(gitRepo.returnBackupReposFile());

        if (!commits.isEmpty()) {
            for (var entry : commits.entrySet()) {
                gitRepo.returnFileSystem().appendArowToTheFile(
                        gitRepo.returnBackupCommitsFile(),
                        entry.getKey() + " " + entry.getValue());
            }
            gitRepo.returnLogger().info("Saving current state : commits cache flushed on the disk");
        }
        if (!dirs.isEmpty()) {
            for (var entry : dirs) {
                gitRepo.returnFileSystem().appendArowToTheFile(
                        gitRepo.returnBackupCommitsFile(),entry.toString());
            }
            gitRepo.returnLogger().info("Saving current state : repositories cache flushed on the disk");
        }
    }

    @Override
    public void recoverPreviousState(MIniGitRepository gitRepo) throws IOException {
        gitRepo.returnFileSystem().removeFile(gitRepo.returnBackupCommitsFile());
        gitRepo.returnFileSystem().removeFile(gitRepo.returnBackupReposFile());
        gitRepo.returnLogger().info("Recovery : both {} and {} have been deleted"
                , gitRepo.returnBackupCommitsFile(), gitRepo.returnBackupReposFile());

        if (!gitRepo.returnFileSystem().isDirExists(gitRepo.returnSourceDir())) {
            gitRepo.returnFileSystem().createDir(gitRepo.returnSourceDir());
        }

        gitRepo.returnCleaner().setSource(gitRepo.returnSourceDir());
        gitRepo.returnFileSystem().deleteRecursively(gitRepo.returnSourceDir(), gitRepo.returnCleaner());

        gitRepo.returnCopier().setSource(gitRepo.returnBackupDataDirectory());
        gitRepo.returnCopier().setTarget(gitRepo.returnSourceDir());
        gitRepo.returnFileSystem().copyRecursively(gitRepo.returnBackupDataDirectory()
                , gitRepo.returnCopier());
        gitRepo.returnLogger().info("Recovery : data copied from backup {} to {}", gitRepo.returnBackupDataDirectory()
                , gitRepo.returnSourceDir());

        if (!commits.isEmpty()) {
            gitRepo.returnCommitsCache().removeCommitsTree(gitRepo.returnSourceDir());
            Queue<String> queue = new LinkedList<>();
            queue.add(gitRepo.returnSourceDir().toString());
            while (!queue.isEmpty()) {
                String curr = queue.poll();
                if (commits.get(curr).isEmpty()) {
                    gitRepo.returnCommitsCache().addCommitToTree(gitRepo.returnSourceDir(), curr);
                    break;
                }
                gitRepo.returnCommitsCache().addCommitToTree(gitRepo.returnSourceDir(), curr);
                queue.add(commits.get(curr));
            }
            gitRepo.returnLogger().info("Recovery : commits' tree recreated in the cache");
        }

        if (!dirs.isEmpty()) {
            ArrayList<Path> recentDirs =
                    new ArrayList<>(gitRepo.returnRepos().returnCachedDirectories());
            for (var value : recentDirs) {
                if (!dirs.contains(value)) {
                    gitRepo.returnRepos().removeFromCache(value);
                }
            }
            for (var value : dirs) {
                if (!recentDirs.contains(value)) {
                    gitRepo.returnRepos().addToCache(value);
                }
            }
            gitRepo.returnLogger().info("Recovery : repositories recreated in the cache");
        }
    }

    @Override
    public void clean(MIniGitRepository gitRepo) throws IOException {
        gitRepo.returnFileSystem().eraseRecursively(gitRepo.returnBackupDirectory(), gitRepo.returnEraser());
        commits = new HashMap<>();
        dirs = new ArrayList<>();
    }
}
