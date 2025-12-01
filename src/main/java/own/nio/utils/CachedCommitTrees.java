package own.nio.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import own.nio.request.CommandsDispatcher;

import javax.sql.rowset.serial.SerialStruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class CachedCommitTrees {
    static HashMap<String, String> commitsTrees = new HashMap<>();
    private static Path treesRoot = Path.of(System.getProperty("user.home")
            + "/commitTrees");
    private static String treesFile = "/commits.txt";
    private static Path treesPath = Path.of(treesRoot + treesFile);
    static public void loadCachedData(){
        try {
            if (!Files.exists(treesRoot)) {
                System.out.println("Directory has been removed " +
                        "by a user between application launches");
                System.out.println("Initializing a resource file on the disk ... ");
                Files.createDirectory(treesRoot);
                Files.createFile(treesPath);
                return;
            }
            List<String> savedDirs = Files.readAllLines(treesPath);
            for (var dir : savedDirs) {
                if (!dir.isEmpty()) {
                    String[] arr = dir.split(" ");
                    if (arr.length == 1) {
                        commitsTrees.put(arr[0], "");
                        continue;
                    }
                    commitsTrees.put(arr[0], arr[1]);
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to read a resource file or " +
                    "to track changes for a miniGit directory");
        }
    }
    static public void unLoadCachedData(){
        try {
            if (Files.exists(treesPath)) {
                Files.delete(treesPath);
                Files.createFile(treesPath);
            }
            for (var entry : returnTrees().entrySet()) {
                Files.writeString(treesPath, entry.getKey() + " " + entry.getValue(), StandardOpenOption.APPEND);
                Files.writeString(treesPath, "\n", StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            System.out.println("Failed to unload working directories");
        }
    }
    static public HashMap<String, String> returnTrees(){
        return commitsTrees;
    }
    static public void removeTree(Path dir){
        String parent = dir.toString();
        if (!commitsTrees.containsKey(parent)) {
            System.out.println("Nothing has been commited yet ..");
            return;
        }
        Queue<String> queue = new LinkedList<>();
        queue.add(parent);

        while (!queue.isEmpty()) {
            String par = queue.poll();
            if (par.isEmpty()) {
                break;
            }
            String next = commitsTrees.get(par);
            commitsTrees.remove(par);
            queue.add(next);
        }
    }
    static public void removeSubTree(String commit, Path commits, String meta){
        if (!commitsTrees.containsKey(commit)) {
            System.out.println("Nothing to remove yet ..");
            return;
        }
        Queue<String> queue = new LinkedList<>();
        queue.add(commitsTrees.get(commit));
        while (!queue.isEmpty()) {
            String par = queue.poll();
            if (par.isEmpty()) {
                break;
            }
            String next = commitsTrees.get(par);
            commitsTrees.remove(par);

            Path dir1 = Path.of(commits + "/" + par);
            dir1 = returnfullPath(dir1, meta);
            try {
                Files.deleteIfExists(dir1);
            } catch (IOException e) {

            }
            queue.add(next);
        }
        commitsTrees.put(commit, "");
    }
    static public String getLastCommit(Path dir){
        String parent = dir.toString();
        Queue<String> queue = new LinkedList<>();
        queue.add(parent);
        while (!queue.isEmpty()) {
            String par = queue.poll();
            String next = commitsTrees.get(par);
            if (next.isEmpty()) {
                return par;
            }
            queue.add(next);
        }
        return "";
    }

    static public boolean isExists(Path dir) {
        return commitsTrees.containsKey(dir.toString());
    }
    static public void addToTree(Path dir, String curr){
        if (isExists(dir)) {
            String last = getLastCommit(dir);
            commitsTrees.put(last, curr);
            commitsTrees.put(curr, "");
        }
        else {
            commitsTrees.put(dir.toString(), curr);
            commitsTrees.put(curr, "");
        }
    }
    static private Path returnfullPath(Path path, String meta){
        ObjectMapper json = new ObjectMapper();
        String longName = "";
        try {
            ObjectNode root = (ObjectNode) json.readTree(Path.of(path + "/" + meta).toFile());
            longName = root.get("long").asText();
        } catch (IOException e) {
        }
        return Path.of(path + "/" + longName);
    }
}
