package cli;

public class GitInitializer {
    public static void launch (){
        System.out.println("Mini Git application starts ... ");
    }
    public static void help (){
        System.out.println("Current list of existing commands : ");
        System.out.println("init, commit, restore, history, diff, gc");
        System.out.println("To know more press {{help}{whitespace}{command}}");
    }
    public static void commandResolver (String str){
        String[] arr = str.split(" ");
        if (arr.length < 2) {
            return;
        }
        String command = arr[1];
        if (command.equals("init")) {
            System.out.println("Initialises Git working directory .vcs");
            System.out.println("Starts tracking file changes in the directory");
            System.out.println("Usage : init {/home/luis/workingDirectory}");
        }
        if (command.equals("commit")) {
            System.out.println("Saves current changes in the directory");
            System.out.println("Git stores the whole history of changes");
            System.out.println("Usage : commit \"message\"");
        }
        if (command.equals("restore")) {
            System.out.println("if not commit identifier is specified, rolls back to a previous working commit");
            System.out.println("otherwise, rolls back changes to a specified commit");
            System.out.println("Usage : restore");
            System.out.println("Usage : restore \"commit identifier\"");
        }
        if (command.equals("history")) {
            System.out.println("lists all the commits in the directory");
            System.out.println("Usage : history");
        }
        if (command.equals("diff")) {
            System.out.println("Shows the difference between two particular commits");
            System.out.println("Usage : diff \"commit identifier 1\" \"commit identifier 2\"");
        }
        if (command.equals("gc")) {
            System.out.println("Cleanse up unused commits");
        }
    }
    public static void finish (){
        System.out.println("Mini Git application is shutting down ... ");
    }
}
