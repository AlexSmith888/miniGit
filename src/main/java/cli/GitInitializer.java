package cli;

import org.apache.logging.log4j.core.Logger;

public class GitInitializer {
    static Logger logger;
    public GitInitializer(Logger logger){
        GitInitializer.logger = logger;
    }
    public static void launch (){
        logger.info("Mini Git application starts ... ");
    }
    public static void help (){
        logger.info("Current list of existing commands : ");
        logger.info("init, commit, restore, history, diff, gc");
        logger.info("To know more press {{help}{whitespace}{command}}");
    }
    public static void commandResolver (String str){
        String[] arr = str.split(" ");
        if (arr.length < 2) {
            return;
        }
        String command = arr[1];
        if (command.equals("init")) {
            logger.info("Initialises Git working directory miniGit under user's working directory");
            logger.info("Starts tracking file changes in the directory");
            logger.info("Usage : init {/target/directory}");
            return;
        }
        if (command.equals("commit")) {
            logger.info("Saves current changes in the directory");
            logger.info("Git stores the whole history of changes");
            logger.info("Usage : commit {whitespace} {/target/directory} {whitespace}\"message\"");
            return;
        }
        if (command.equals("restore")) {
            logger.info("if not commit identifier is specified, rolls back to a previous working commit");
            logger.info("otherwise, rolls back changes to a specified commit");
            logger.info("Usage : restore");
            logger.info("Usage : restore {whitespace} {/target/directory}\"commit identifier\"");
            return;
        }
        if (command.equals("history")) {
            logger.info("lists all the commits in the directory");
            logger.info("Usage : history {whitespace} {/target/directory}");
            return;
        }
        if (command.equals("diff")) {
            logger.info("Shows the difference between two particular commits");
            logger.info("Usage : diff {whitespace} \"commit identifier 1\" {whitespace}\" commit identifier 2\"");
            return;
        }
        if (command.equals("undo")) {
            logger.info("Deletes miniGit if existed");
            logger.info("Usage : undo {whitespace} {/target/directory}");
        }
        if (command.equals("track")) {
            logger.info("Prepares data to be commited");
            logger.info("Transfers data from a target directory to a staging area");
            logger.info("Usage : track {whitespace} {/target/directory}");
        }
    }
    public static void finish (){
        logger.info("Mini Git application is shutting down ... ");
    }
}
