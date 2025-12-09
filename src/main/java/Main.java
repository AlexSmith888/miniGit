import app.state.StateManager;
import cli.GitInitializer;
import domain.services.RequestsDispatcher;
import infrastructure.cache.CachedRepositories;
import infrastructure.cache.CommitsCache;
import infrastructure.cache.CommitsCacheUseCases;
import infrastructure.encryption.PathCipher;
import infrastructure.encryption.PathsEncryption;
import infrastructure.entities.*;
import infrastructure.filescomparison.ByteToByteComparator;
import infrastructure.filescomparison.FilesComparator;
import infrastructure.filesystem.Cleaner;
import infrastructure.filesystem.Copier;
import infrastructure.filesystem.Eraser;
import infrastructure.filesystem.Viewer;
import infrastructure.storage.JsonEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import utils.CLiParser;
import app.validations.InputValidation;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static final Logger logger = (Logger) LogManager.getLogger(Main.class);
    public static void main(String[] args) throws IOException {
        GitInitializer initializer = new GitInitializer(logger);
        initializer.launch();
        FileSystemGateway fsGate = new LocalFsTasksExecutor(logger);
        CommitsCacheLoader commitsCache = new CommitsCache(fsGate);
        RepositoriesGateway repoGate = new CachedRepositories(fsGate);
        CommitsCacheGateway commitsGW = new CommitsCacheUseCases(
                commitsCache.returnCurrentState(), fsGate);

        FilesComparator diff = new ByteToByteComparator();
        Copier copier = new Copier();
        Eraser eraser = new Eraser();
        Cleaner cleaner = new Cleaner();
        Viewer viewer = new Viewer(diff);

        JsonEntity jsonFile = new JsonEntity();
        PathsEncryption encrypt = new PathCipher();
        StateManager state = new StateManager();

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
            if (text.startsWith("help")) {
                initializer.commandResolver(text);
                continue;
            }
            try {
                new InputValidation().isValid(
                        CLiParser.returnInitInput(text));
                new RequestsDispatcher().process(
                        CLiParser.returnInitInput(text)
                        , commitsGW
                        , repoGate
                        , fsGate
                        , copier
                        , eraser
                        , cleaner
                        , viewer
                        , jsonFile
                        , encrypt
                        , state
                        , logger);
            } catch (IllegalArgumentException e) {
                logger.warn("Illegal input parameters");
                logger.warn(e.getMessage());
            } catch (IOException e) {
                logger.error("Impossible to read / copy / change directories " +
                        "/ files under miniGit maintenance ");
                logger.error(e.getMessage());
            } catch (RuntimeException e) {
                logger.error(e.getMessage());
            }
        }

        scanner.close();
        commitsCache.flushToTheDisk();
        repoGate.unLoadCachedDirs();
        initializer.finish();
    }
}
