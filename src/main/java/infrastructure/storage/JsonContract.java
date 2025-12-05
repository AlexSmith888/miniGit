package infrastructure.storage;

public interface JsonContract {
    void setShortFolderIdentifier(JsonEntity entity, String value);
    void setLongFolderIdentifier(JsonEntity entity, String value);
    void setFullFolderIdentifier(JsonEntity entity, String value);
    void setEventTimestamp(JsonEntity entity, String value);
    void setCommitMessage(JsonEntity entity, String value);
    void setCommitSourceFolder(JsonEntity entity, String value);
}
