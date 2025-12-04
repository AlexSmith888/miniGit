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

public class Restore implements Request {
    //exists in utils and somewhere else
    private Path returnfullPath(Path path, String meta){
        ObjectMapper json = new ObjectMapper();
        String longName = "";
        try {
            ObjectNode root = (ObjectNode) json.readTree(Path.of(path + "/" + meta).toFile());
            longName = root.get("long").asText();
        } catch (IOException e) {
        }
        return Path.of(path + "/" + longName);
    }

    @Override
    public void execute(MIniGitRepository entity) throws IOException {
        Path source = Path.of(entity.returnSourceGitCommitDir()
                + "/" + entity.returnCommitShort1());
        source = returnfullPath(source, entity.returnMetaFile());

        try {
            //track changes commit directory ---> temp
            entity.returnCleaner().setSource(entity.returnSourceGitTempDir());
            entity.returnFileSystem().deleteRecursively(entity.returnSourceGitTempDir()
                    , entity.returnCleaner());

            entity.returnCopier().setSource(source);
            entity.returnCopier().setTarget(entity.returnSourceGitTempDir());
            entity.returnFileSystem().copyRecursively(source, entity.returnCopier());
            //track changes temp ---> mini git user directory
            entity.returnCleaner().setSource(entity.returnSourceDir());
            entity.returnCleaner().addToExcludedList(entity.returnSourceGitDir());
            entity.returnFileSystem().deleteRecursively(entity.returnSourceDir()
                    , entity.returnCleaner());

            entity.returnCopier().setSource(entity.returnSourceGitTempDir());
            entity.returnCopier().setTarget(entity.returnSourceDir());
            entity.returnFileSystem().copyRecursively(entity.returnSourceGitTempDir()
                    , entity.returnCopier());

            HashMap<String, String> map = entity.returnCommitsCache()
                    .retrieveSubtree(entity.returnCommitShort1());
            //recover commit tree to the target commit
            entity.returnCommitsCache().removeCommitsSubTree(entity.returnCommitShort1()
                    ,entity.returnSourceGitCommitDir()
                    ,entity.returnMetaFile());

            Queue<String> queue = new LinkedList<>();
            queue.add(map.get(entity.returnCommitShort1()));
            while (!queue.isEmpty()) {
                String parent = queue.poll();
                if (parent.isEmpty()) {
                    break;
                }
                String child = map.get(parent);
                entity.returnFileSystem().eraseRecursively(
                        Path.of(entity.returnSourceGitCommitDir()
                                + "/" + parent),
                        entity.returnEraser()
                );
                queue.add(child);
            }

        } catch (IOException e) {
            System.out.println("Impossible to restore repo to the state of "
                    + entity.returnCommitShort1());
            System.out.println(e.getMessage());
            throw e;
        }
    }
}
