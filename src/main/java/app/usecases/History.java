package app.usecases;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import domain.services.Request;
import domain.entities.MIniGitRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class History implements Request {
    @Override
    public void execute(MIniGitRepository entity) throws IOException {
        String parent = entity.returnSourceDir().toString();
        Queue<String> queue = new LinkedList<>();
        queue.add(parent);
        HashMap<String, String> mapa = entity.returnCommitsCache()
                .retrieveSubtree(parent);

        while (!queue.isEmpty()) {
            String par = queue.poll();
            String next = mapa.get(par);
            if (next.isEmpty()) {
                continue;
            }
            Path commit = Path.of(entity.returnSourceGitCommitDir()
                    + "/" + next + "/" + entity.returnMetaFile());
            entity.returnFileSystem().printJson(commit);
            queue.add(next);
        }
    }
}
