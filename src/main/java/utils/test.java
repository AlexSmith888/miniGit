package utils;

import infrastructure.cache.CommitsCache;
import infrastructure.entities.LocalFsTasksExecutor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class test {
    public static void main(String[] args) throws IOException {
        LocalFsTasksExecutor fs = new LocalFsTasksExecutor();
        Path pt = Path.of("/home/alex/d");
        Path pt1 = Path.of("/home/alex/d/5.txt");
        Files.createDirectory(pt);
        Files.createFile(pt1);
        Files.delete(pt);

    }
}
