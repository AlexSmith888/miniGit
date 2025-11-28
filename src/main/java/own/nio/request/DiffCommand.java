package own.nio.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import own.nio.core.Command;
import own.nio.core.MIniGitClass;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DiffCommand implements Command {
    private Path returnfullPath(Path path, String meta){
        ObjectMapper json = new ObjectMapper();
        String longName = "";
        try {
            ObjectNode root = (ObjectNode) json.readTree(path.toString() + "/" + meta);
            System.out.println(path.toString() + "/" + meta);
            longName = root.get("long").asText();
        } catch (IOException e) {
        }
        return Path.of(path + "/" + longName);
    }
    @Override
    public void execute(MIniGitClass entity) throws IOException {

        Path dir1 = Path.of(entity.returnSourceGitCommitDir()
                + "/" + entity.returnCommitShort1());
        Path dir2 = Path.of(entity.returnSourceGitCommitDir()
                + "/" + entity.returnCommitShort2());

        dir1 = returnfullPath(dir1, entity.returnMetaFile());
        dir2 = returnfullPath(dir2, entity.returnMetaFile());

        System.out.println(dir1 + " " + dir2 + " are compared ");

        Files.walkFileTree(dir1,
                new DiffDirectoryTree(dir1, entity.returnCommitShort1(), entity.returnCommitShort2()));
        Files.walkFileTree(dir2,
                new DiffDirectoryTree(dir2, entity.returnCommitShort2(), entity.returnCommitShort1()));
    }
}
