import cli.GitInitializer;
import domain.services.RequestsDispatcher;
import infrastructure.cache.CachedRepositories;
import infrastructure.cache.CommitsCache;
import infrastructure.cache.CommitsCacheUseCases;
import infrastructure.entities.*;
import utils.CLiParser;
import app.validations.InputValidation;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        GitInitializer.launch();
        FileSystemGateway fsGate = new LocalFsTasksExecutor();
        CommitsCacheLoader commitsCache = new CommitsCache(fsGate);
        RepositoriesGateway repoGate = new CachedRepositories(fsGate);
        CommitsCacheGateway commitsGW = new CommitsCacheUseCases(
                commitsCache.returnCurrentState(),
                fsGate
        );

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
                        CLiParser.returnInitInput(text), commitsGW, repoGate, fsGate);

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
