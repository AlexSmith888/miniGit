public class GitInitializer {
    public static void launch (){
        IO.println("Mini Git application starts ... ");
    }
    public static void help (){
        IO.println("Current list of existing commands : ");
        IO.println("init, commit, restore, history, diff, gc");
        IO.println("To know more press {{help}{whitespace}{command}}");
    }
    public static void commandResolver (String str){
        String[] arr = str.split(" ");
        if (arr.length < 2) {
            return;
        }
        String command = arr[1];
        if (command.equals("init")) {
            IO.println("Initialises Git working directory .vcs");
            IO.println("Starts tracking file changes in the directory");
            IO.println("Usage : init {/home/luis/workingDirectory}");
        }
        if (command.equals("commit")) {
            IO.println("Saves current changes in the directory");
            IO.println("Git stores the whole history of changes");
            IO.println("Usage : commit \"message\"");
        }
        if (command.equals("restore")) {
            IO.println("if not commit identifier is specified, rolls back to a previous working commit");
            IO.println("otherwise, rolls back changes to a specified commit");
            IO.println("Usage : restore");
            IO.println("Usage : restore \"commit identifier\"");
        }
        if (command.equals("history")) {
            IO.println("lists all the commits in the directory");
            IO.println("Usage : history");
        }
        if (command.equals("diff")) {
            IO.println("Shows the difference between two particular commits");
            IO.println("Usage : diff \"commit identifier 1\" \"commit identifier 2\"");
        }
        if (command.equals("gc")) {
            IO.println("Cleanse up unused commits");
        }
    }
    public static void finish (){
        System.out.println("Mini Git application is shutting down ... ");
    }
}
