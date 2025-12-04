import cli.GitInitializer;
import domain.services.RequestsDispatcher;
import infrastructure.cache.CachedRepositories;
import infrastructure.cache.CommitsCache;
import infrastructure.cache.CommitsCacheUseCases;
import infrastructure.entities.*;
import infrastructure.filescomparison.ByteToByteComparator;
import infrastructure.filescomparison.FilesComparator;
import infrastructure.filesystem.Cleaner;
import infrastructure.filesystem.Copier;
import infrastructure.filesystem.Eraser;
import infrastructure.filesystem.Viewer;
import utils.CLiParser;
import app.validations.InputValidation;

import java.io.IOException;
import java.util.Comparator;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        GitInitializer.launch();
        FileSystemGateway fsGate = new LocalFsTasksExecutor();
        CommitsCacheLoader commitsCache = new CommitsCache(fsGate);
        RepositoriesGateway repoGate = new CachedRepositories(fsGate);
        CommitsCacheGateway commitsGW = new CommitsCacheUseCases(
                commitsCache.returnCurrentState(), fsGate);

        FilesComparator diff = new ByteToByteComparator();
        Copier copier = new Copier();
        Eraser eraser = new Eraser();
        Cleaner cleaner = new Cleaner();
        Viewer viewer = new Viewer(diff);

        commitsCache.loadInMemory();
        repoGate.loadCachedDirs();
        Scanner scanner = new Scanner(System.in);
        String text;

        while (true) {
            GitInitializer.help();
            System.out.println(" > ");
            text = scanner.nextLine().trim().toLowerCase();

            if (text.equals("exit")) {
                break;
            }

            try {
                new InputValidation().isValid(
                        CLiParser.returnInitInput(text));
                new RequestsDispatcher().process(
                        CLiParser.returnInitInput(text), commitsGW
                        ,repoGate, fsGate, copier, eraser, cleaner, viewer);

            } catch (IllegalArgumentException e) {
                System.out.println("Illegal input parameters");
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println("Check whether directories / files exist");
                System.out.println(e.getMessage());
            }
        }

        scanner.close();
        commitsCache.flushToTheDisk();
        repoGate.unLoadCachedDirs();
        GitInitializer.finish();
    }
}
