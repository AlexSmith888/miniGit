package app.usecases;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import domain.services.Request;
import domain.entities.MIniGitRepository;

import java.io.IOException;
import java.nio.file.Path;

public class Diff implements Request {
    private Path returnfullPath(Path path, String meta){
        ObjectMapper json = new ObjectMapper();
        String longName = "";
        try {
            ObjectNode root = (ObjectNode) json.readTree(Path.of(path.toString() + "/" + meta).toFile());
            longName = root.get("long").asText();
        } catch (IOException e) {
        }
        return Path.of(path + "/" + longName);
    }
    @Override
    public void execute(MIniGitRepository entity) throws IOException {

        try {
            Path dir1 = Path.of(entity.returnSourceGitCommitDir()
                    + "/" + entity.returnCommitShort1());
            Path dir2 = Path.of(entity.returnSourceGitCommitDir()
                    + "/" + entity.returnCommitShort2());

            Path fullPath1 = returnfullPath(dir1, entity.returnMetaFile());
            Path fullPath2 = returnfullPath(dir2, entity.returnMetaFile());

            entity.returnViewer().setSource(fullPath1);
            entity.returnViewer().setTarget(fullPath2);
            entity.returnFileSystem().viewDifference(fullPath1, entity.returnViewer());
        } catch (IOException e) {
            System.out.println("Failed to complete diff request ");
            throw e;
        }
    }
}
