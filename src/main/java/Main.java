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
    /*
    CLI utility
    press exit to quit
    press help {command} to get instructions
    list of currently supported commands is in /domain/services/requests ENUM
    A user should be careful with spelling, especially with whitespaces
    and double quotes that are reserved for commit messages, but not for naming conventions
    * */
    private static final Logger logger = (Logger) LogManager.getLogger(Main.class);
    public static void main(String[] args) throws IOException {
        //General information
        GitInitializer initializer = new GitInitializer(logger);
        initializer.launch();
        //an abstract layer to interact with the filesystem
        FileSystemGateway fsGate = new LocalFsTasksExecutor(logger);
        //loads commits in memory cache
        CommitsCacheLoader commitsCache = new CommitsCache(fsGate);
        //loads repositories in memory cache
        RepositoriesGateway repoGate = new CachedRepositories(fsGate);
        //functionality to interact with commit cache while app runs
        CommitsCacheGateway commitsGW = new CommitsCacheUseCases(
                commitsCache.returnCurrentState(), fsGate);

        //byte - to -byte files comparison
        FilesComparator diff = new ByteToByteComparator();
        //copy recursively with tuned parameters
        Copier copier = new Copier();
        //erase directory completely up to root one
        Eraser eraser = new Eraser();
        //clean directory with tuned parameters
        Cleaner cleaner = new Cleaner();
        //view directory internals with tuned parameters
        Viewer viewer = new Viewer(diff);

        //serialize / deserialize json files based on contract
        JsonEntity jsonFile = new JsonEntity();
        //use SHA-1 to has data
        PathsEncryption encrypt = new PathCipher();
        //safe recovery after incomplete requests
        //if something goes wrong, the app returns safely to a previous working state
        //without applying partial / incomplete changes
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
                //parses an input , can be tuned based on growing requirements
                new InputValidation().isValid(
                        CLiParser.returnInitInput(text));
                //main entity , depends on many injections
                //uses builder patterns for convenience and extensibility
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
