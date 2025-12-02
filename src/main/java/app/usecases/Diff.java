package app.usecases;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import domain.services.Request;
import domain.entities.MIniGitRepository;
import infrastructure.filesystem.DiffDirectoryTree;

import java.io.IOException;
import java.nio.file.Files;
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

        Path dir1 = Path.of(entity.returnSourceGitCommitDir()
                + "/" + entity.returnCommitShort1());
        Path dir2 = Path.of(entity.returnSourceGitCommitDir()
                + "/" + entity.returnCommitShort2());

        Path fullPath1 = returnfullPath(dir1, entity.returnMetaFile());
        Path fullPath2 = returnfullPath(dir2, entity.returnMetaFile());

        String tobeReplaced1 = entity.returnCommitShort1() + "/" + fullPath1.getFileName();
        String tobeReplaced2 = entity.returnCommitShort2() + "/" + fullPath2.getFileName();

        Files.walkFileTree(fullPath1,
                new DiffDirectoryTree(fullPath1, tobeReplaced1, tobeReplaced2));
        Files.walkFileTree(fullPath2,
                new DiffDirectoryTree(fullPath2, tobeReplaced2, tobeReplaced1));

    }
}
