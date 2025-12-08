package infrastructure.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import infrastructure.entities.CommitsCacheGateway;
import infrastructure.entities.FileSystemGateway;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class CommitsCacheUseCases implements CommitsCacheGateway {
    private HashMap<String, String> commits;
    private FileSystemGateway fsGateway;

    public CommitsCacheUseCases(HashMap<String, String> commits, FileSystemGateway FsGateway) {
        this.commits = commits;
        this.fsGateway = FsGateway;
    }

    @Override
    public void removeCommitsTree(Path dir) {
        String parent = dir.toString();
        if (!commits.containsKey(parent)) {
            System.out.println("Commit does not exists");
            return;
        }

        Queue<String> queue = new LinkedList<>();
        queue.add(parent);

        while (!queue.isEmpty()) {
            String par = queue.poll();
            if (par.isEmpty()) {
                break;
            }
            String next = commits.get(par);
            commits.remove(par);
            queue.add(next);
        }
    }

    @Override
    public void removeCommitsSubTree(String commit, Path commitsTree, String meta) throws IOException {
        if (!commits.containsKey(commit)) {
            System.out.println("Nothing to remove yet ..");
            return;
        }
        Queue<String> queue = new LinkedList<>();
        queue.add(commits.get(commit));
        while (!queue.isEmpty()) {
            String par = queue.poll();
            if (par.isEmpty()) {
                break;
            }
            String next = commits.get(par);
            commits.remove(par);
            queue.add(next);
        }
        commits.put(commit, "");
    }

    @Override
    public String getLastCommitForParent(Path dir) {

        String parent = dir.toString();
        Queue<String> queue = new LinkedList<>();
        queue.add(parent);

        while (!queue.isEmpty()) {
            String par = queue.poll();
            String next = commits.get(par);
            if (next.isEmpty()) {
                return par;
            }
            queue.add(next);
        }

        return "";
    }

    @Override
    public boolean isCommitExists(Path dir) {
        return commits.containsKey(dir.toString());
    }

    @Override
    public void addCommitToTree(Path dir, String value) {
        if (isCommitExists(dir)) {
            String last = getLastCommitForParent(dir);
            commits.put(last, value);
            commits.put(value, "");
        }
        else {
            commits.put(dir.toString(), value);
            commits.put(value, "");
        }
    }

    @Override
    public HashMap<String, String> retrieveSubtree(String dir) {
        if (commits.isEmpty() || !commits.containsKey(dir)) {
            return new HashMap<>();
        }

        HashMap<String, String> map = new HashMap<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(dir);

        while (!queue.isEmpty()) {
            String par = queue.poll();
            if (par.isEmpty()) {
                return map;
            }
            String next = commits.get(par);
            if (next.isEmpty()) {
                map.put(par, "");
                return map;
            }
            map.put(par, next);
            queue.add(next);
        }

        return map;
    }
}