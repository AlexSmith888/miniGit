package infrastructure.storage;

public interface JsonData {
    public String returnShortFolderIdentifier();
    public String returnLongFolderIdentifier();
    public String returnFullFolderIdentifier();
    public String returnEventTimestamp();
    public String returnCommitMessage();
    public String returnCommitSourceFolder();
}
