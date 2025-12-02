package domain.services;

public enum Requests {

    COMMIT("commit"),
    INIT("init"),
    HISTORY("history"),
    DIFF("diff"),
    TRACK("track"),
    UNDO("undo"),
    RESTORE("restore");

    private final String comm;
    Requests(String comm) {
        this.comm = comm;
    }
    public String get(){
        return comm;
    }
}
