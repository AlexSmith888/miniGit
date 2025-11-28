package own.nio.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import own.nio.core.Command;
import own.nio.core.MIniGitClass;
import own.nio.utils.CachedCommitTrees;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class HistoryCommand implements Command {
    @Override
    public void execute(MIniGitClass entity) throws IOException {
        String parent = entity.returnSourceDir().toString();
        Queue<String> queue = new LinkedList<>();
        queue.add(parent);
        HashMap<String, String> mapa = CachedCommitTrees.returnTrees();

        while (!queue.isEmpty()) {
            String par = queue.poll();
            String next = mapa.get(par);
            if (next.isEmpty()) {
                continue;
            }
            Path commit = Path.of(entity.returnSourceGitCommitDir()
                    + "/" + next + "/" + entity.returnMetaFile());
            printInfo(commit);
            queue.add(next);
        }
    }
    public void  printInfo(Path path){
        ObjectMapper json = new ObjectMapper();
        try {

            ObjectNode root = (ObjectNode) json.readTree(path.toFile());
            String shortname = root.get("short").asText();
            String hash = root.get("commit").asText();
            String timestamp = root.get("timestamp").asText();
            String source = root.get("source").asText();
            String message = root.get("message").asText();

            System.out.println(shortname + " " + message);
            System.out.println(source + " " + timestamp);
            System.out.println(hash);

        } catch (IOException e) {
            System.out.println("Impossible to read files in the directory : "
                    + path.toFile());
        }
    }
}
