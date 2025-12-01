package own.nio.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import own.nio.core.Command;
import own.nio.core.MIniGitClass;
import own.nio.utils.CachedCommitTrees;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RestoreCommand implements Command {
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
    public void execute(MIniGitClass entity) throws IOException {

        Path source = Path.of(entity.returnSourceGitCommitDir()
                + "/" + entity.returnCommitShort1());
        source = returnfullPath(source, entity.returnMetaFile());
        Path vcsDirectory = entity.returnSourceGitDir();
        Path targetMain = entity.returnSourceDir();
        Path targetTemp = entity.returnSourceGitTempDir();

        try {
            //track changes commit directory ---> temp
            Files.walkFileTree(targetTemp, new PurgeTempDirectoryTree(targetTemp));
            Files.walkFileTree(source, new CopyDirectoryTree(source, targetTemp));
            //track changes temp ---> mini git directory
            Files.walkFileTree(targetMain, new PurgeDirectoryTree(vcsDirectory, targetMain));
            Files.walkFileTree(targetTemp, new CopyDirectoryTree(targetTemp, targetMain));
        } catch (IOException e) {
            IO.println("Impossible to track changes");
            System.out.println(e.getMessage());
            throw e;
        }

        //Path dir, Path commits, String meta
        CachedCommitTrees.removeSubTree(entity.returnCommitShort1()
                ,entity.returnSourceGitCommitDir(), entity.returnMetaFile());

    }
}
