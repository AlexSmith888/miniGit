package commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import own.nio.request.InitCommand;
import own.nio.request.MoveDirectoryTree;
import own.nio.utils.CachedDirectories;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class InitCommandTests {
    InitCommand instance;
    List<Path> miniGitDirectories = new ArrayList<>();
    @BeforeEach
    void setUp(){
        miniGitDirectories = new ArrayList<>();
        instance = new InitCommand();
    }
    @Test
    @DisplayName("Cached data should be accessible during runtime")
    void assertCachedRootFoldersAreLoaded(){
        assertTrue(miniGitDirectories.size() == 0);
        Path source = Path.of(System.getProperty("user.home") + "/"
                + " " + System.currentTimeMillis());
        miniGitDirectories.add(source);
        assertTrue(miniGitDirectories.size() == 1);
        miniGitDirectories.remove(source);
        assertTrue(miniGitDirectories.size() == 0);
    }

    @Test
    @DisplayName("Should recreate a source directory tree under miniGit directory")
    void assertCommandSucceeds() throws IOException {
        Path source = Path.of(System.getProperty("user.home") + "/"
                + " " + System.currentTimeMillis());

        Files.createDirectory(source);
        new CachedDirectories(miniGitDirectories);

        instance.execute(new String[]{"init", source.toString()});


        Path vcsFolder = source.resolve("miniGit");
        Path workingArea = vcsFolder.resolve("temp");
        Path commitsTree = vcsFolder.resolve("commits");

        assertTrue(Files.exists(vcsFolder));
        assertTrue(Files.exists(workingArea));
        assertTrue(Files.exists(commitsTree));
        assertTrue(CachedDirectories.returnDirectories().contains(source));

        CachedDirectories.returnDirectories().remove(source);
        Files.delete(commitsTree);
        Files.delete(workingArea);
        Files.delete(vcsFolder);
        Files.delete(source);
    }

    public void createDirs(List<Path> paths) throws IOException {
        for (var x : paths) {
            Files.createDirectory(x);
        }
    }
    public void createFiles(List<Path> paths) throws IOException {
        for (var x : paths) {
            Files.createFile(x);
        }
    }
    public void deletePaths(List<Path> paths) throws IOException {
        for (var x : paths) {
            Files.delete(x);
        }
    }
    public List<Path> createAndReturn(List<Path> relative, List<String> variables){
        List<Path> arr = new ArrayList<>();
        for (var x : relative) {
            for (var y : variables) {
                Path curr = Path.of(x + y);
                arr.add(curr);
            }
        }
        return arr;
    }

    @Test
    @DisplayName("All files in the root directory should be transferred to miniGit folder")
    void assertInitCommandSucceeds() throws IOException {
        Path directory = Path.of(System.getProperty("user.home")
                + "/" + System.currentTimeMillis());

        List<Path> dirs = createAndReturn(List.of(directory), List.of("/a", "/b", "/c"));
        List<Path> files = createAndReturn(dirs, List.of("/1.txt"));
        Files.createDirectory(directory);
        createDirs(dirs);
        createFiles(files);

        new CachedDirectories(miniGitDirectories);
        instance.execute(new String[]{"init", directory.toString()});

        Path vcsFolder = directory.resolve("miniGit");
        Path workingArea = vcsFolder.resolve("temp");
        Path commitsTree = vcsFolder.resolve("commits");

        assertTrue(CachedDirectories.returnDirectories().contains(directory));
        assertTrue(Files.exists(workingArea));
        assertTrue(Files.exists(commitsTree));
        assertTrue(Files.exists(vcsFolder));

        List<Path> tempdirs = createAndReturn(List.of(workingArea), List.of("/a", "/b", "/c"));
        List<Path> tempfiles = createAndReturn(tempdirs, List.of("/1.txt"));

        for (var x : tempdirs) {
            assertTrue(Files.exists(x));
        }
        for (var x : tempfiles) {
            assertTrue(Files.exists(x));
        }

        deletePaths(tempfiles);
        deletePaths(files);
        deletePaths(tempdirs);
        deletePaths(dirs);

        deletePaths(List.of(workingArea, commitsTree, vcsFolder));
        Files.delete(directory);
    }
}
