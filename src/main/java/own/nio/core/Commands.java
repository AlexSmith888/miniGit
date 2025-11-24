package own.nio.core;

public enum Commands {

    COMMIT("commit"),
    INIT("init"),
    HISTORY("history"),
    DIFF("diff"),
    TRACK("track"),
    RESTORE("restore");

    private final String comm;
    Commands(String comm) {
        this.comm = comm;
    }
    public String get(){
        return comm;
    }
}
