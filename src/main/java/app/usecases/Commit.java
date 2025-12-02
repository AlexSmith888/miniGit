package app.usecases;

import com.fasterxml.jackson.databind.ObjectMapper;
import infrastructure.filesystem.MoveCommitTree;
import domain.services.Request;
import domain.entities.MIniGitRepository;
import infrastructure.cache.CachedCommitTrees;
import infrastructure.encryption.EncryptCommitPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class Commit implements Request {

    @Override
    public void execute(MIniGitRepository entity) throws IOException {

        EncryptCommitPaths.process(entity.returnSourceDir());
        String shortIndentifier =  EncryptCommitPaths.getShortHash();
        String longIndentifier = EncryptCommitPaths.getLongHash();
        String fullHash = EncryptCommitPaths.getHash();

        Path mainCommitDirectory ;
        Path mainDataCommitDirectory;
        try {
            mainCommitDirectory = entity.returnSourceGitCommitDir().resolve(shortIndentifier);
            mainDataCommitDirectory = mainCommitDirectory.resolve(longIndentifier);
            Files.createDirectory(mainCommitDirectory);
            Files.createDirectory(mainDataCommitDirectory);
        } catch (IOException e) {
            System.out.println("The issue while creating a commit directory");
            throw e;
        }

        try {
            String replacedDirectory = "temp";
            String tobeRepalcedWith = "commits/"
                    + shortIndentifier + "/" + longIndentifier;

            Files.walkFileTree(entity.returnSourceGitTempDir(),
                    new MoveCommitTree(
                            entity.returnSourceGitTempDir()
                            , replacedDirectory
                            , tobeRepalcedWith));
        } catch (IOException e) {
            System.out.println("Impossible to commit changes");
            System.out.println(e.getMessage());
            throw e;
        }

        String tmstp = String.valueOf(System.currentTimeMillis());
        Map<String, String> data = Map.of(
                "short", shortIndentifier,
                "long", longIndentifier,
                "commit", fullHash,
                "timestamp", tmstp,
                "source", entity.returnSourceDir().toString(),
                "message", entity.returnCommitMessage()
        );

        writeJsonToTheDisk(mainCommitDirectory, data);
        CachedCommitTrees.addToTree(entity.returnSourceDir(), shortIndentifier);
    }
    public void writeJsonToTheDisk (Path mainCommitDirectory, Map<String, String> data){
        ObjectMapper mapper = new ObjectMapper();
        Path file = Path.of(mainCommitDirectory + "/meta.json");
        try {

            if (Files.exists(file)) {
                System.out.println("The same file exist ... ");
            }

            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(file.toFile(), data);
            System.out.println("Attempting to save " +
                    "a json file on the disk : " + file);
        } catch (IOException e) {
            System.out.println("Failed to create a Json file");
        }
    }
}
