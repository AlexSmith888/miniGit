package infrastructure.entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import infrastructure.storage.JsonData;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.util.Loader;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

public class LocalFsTasksExecutor implements FileSystemGateway{
    private final Logger logger;

    public LocalFsTasksExecutor(Logger logger) {
        this.logger = logger;
    }
    @Override
    public void createDir(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
    }

    @Override
    public void removeDir(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            List<Path> ls = Files.list(path).toList();
            if (ls.isEmpty()) {
                Files.delete(path);
            }
        }
    }

    @Override
    public void copyDir(Path source, Path destination) throws IOException {
        Path target = Path.of(destination + "/" + source.getFileName());
        if (!Files.exists(target)) {
            Files.copy(source, target);
        }
    }
    @Override
    public void copyDirWithSuffix(Path source, Path destination) throws IOException {
        Path target = Path.of(destination
                + "/" + source.getFileName() + "_" + System.currentTimeMillis());
        if (!Files.exists(target)) {
            Files.copy(source, target);
        }
    }

    @Override
    public List<Path> listOfObjectsInFolder(Path source) throws IOException {
        if (!Files.isDirectory(source)) {
            throw new IOException("Not a directory");
        }
        return Files.list(source).toList();
    }

    @Override
    public void createFile(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
    }

    @Override
    public void removeFile(Path path) throws IOException {
        if (Files.exists(path) && Files.isRegularFile(path)) {
            Files.delete(path);
        }
    }

    @Override
    public void copyFile(Path source, Path destination) throws IOException {
        if (Files.isRegularFile(destination)) {
            destination = destination.getParent();
        }
        Path target = Path.of(destination + "/" + source.getFileName());
        if (Files.exists(target)) {
            Files.delete(target);
        }
        Files.createFile(target);
    }

    @Override
    public boolean isDirExists(Path source) throws IOException {
        return Files.exists(source) && Files.isDirectory(source);
    }

    @Override
    public boolean isFileExists(Path source) throws IOException {
        return Files.exists(source) && Files.isRegularFile(source);
    }

    @Override
    public void appendArowToTheFile(Path source, String row) throws IOException {
        if (isFileExists(source)) {
            Files.writeString(source, row, StandardOpenOption.APPEND);
            Files.writeString(source, "\n", StandardOpenOption.APPEND);
        }
    }
    @Override
    public List<String> readTheFile(Path path) throws IOException {
        return Files.readAllLines(path);
    }

    @Override
    public void copyRecursively(Path source, FileVisitor worker) throws IOException {
        Files.walkFileTree(source, worker);
    }

    @Override
    public void deleteRecursively(Path source, FileVisitor worker) throws IOException {
        Files.walkFileTree(source, worker);
    }
    @Override
    public void eraseRecursively(Path source, FileVisitor worker) throws IOException {
        Files.walkFileTree(source, worker);
    }

    @Override
    public void viewDifference(Path source, FileVisitor worker) throws IOException {
        Files.walkFileTree(source, worker);
    }

    @Override
    public void writeJsonToTheDisk(Path source, JsonData file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> data = Map.of(
                "short", file.returnShortFolderIdentifier() == null ? "" : file.returnShortFolderIdentifier(),
                "long", file.returnLongFolderIdentifier() == null ? "" : file.returnLongFolderIdentifier(),
                "commit", file.returnFullFolderIdentifier() == null ? "" : file.returnFullFolderIdentifier(),
                "timestamp", file.returnEventTimestamp() == null ? "" : file.returnEventTimestamp(),
                "source", file.returnCommitSourceFolder() == null ? "" : file.returnCommitSourceFolder(),
                "message", file.returnCommitMessage() == null ? "" : file.returnCommitMessage()
        );

        try {
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(source.toFile(), data);
            logger.info("Json metadata file created on the disk : {}"
                    , source.getFileName());
        } catch (IOException e) {
            logger.error("Failed to create a Json file");
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public Path returnfullPath(Path path, String fileName) throws IOException{
        ObjectMapper json = new ObjectMapper();
        String longName = "";
        try {
            ObjectNode root = (ObjectNode) json.readTree(Path.of(path + "/" + fileName).toFile());
            longName = root.get("long").asText();
        } catch (IOException e) {
            logger.error("Impossible to retrieve a requested Json file {}", fileName);
            logger.error(e.getMessage());
            throw e;
        }
        return Path.of(path + "/" + longName);
    }

    @Override
    public void printJson(Path path) throws IOException {
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
            logger.error("Impossible to read a Json file in the directory : {}", path.toFile());
            throw e;
        }
    }
}
