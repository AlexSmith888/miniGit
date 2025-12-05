package infrastructure.storage;

public class JsonEntity implements JsonData {

    private String shortFolderIdentifier = "";
    private String longFolderIdentifier = "";
    private String fullFolderIdentifier = "";
    private String eventTimestamp = "";
    private String commitMessage = "";
    private String commitSourceFolder = "";

    public void setShortFolderIdentifier(String value){
        this.shortFolderIdentifier = value;
    }
    public void setlongFolderIdentifier(String value){
        this.longFolderIdentifier = value;
    }
    public void setfullFolderIdentifier(String value){
        this.fullFolderIdentifier = value;
    }
    public void seteventTimestamp(String value){
        this.eventTimestamp = value;
    }
    public void setCommitMessage(String value){
        this.commitMessage = value;
    }
    public void setcommitSourceFolder(String value){
        this.commitSourceFolder = value;
    }

    @Override
    public String returnShortFolderIdentifier() {
        return shortFolderIdentifier;
    }

    @Override
    public String returnLongFolderIdentifier() {
        return longFolderIdentifier;
    }

    @Override
    public String returnFullFolderIdentifier() {
        return fullFolderIdentifier;
    }

    @Override
    public String returnEventTimestamp() {
        return eventTimestamp;
    }

    @Override
    public String returnCommitMessage() {
        return commitMessage;
    }

    @Override
    public String returnCommitSourceFolder() {
        return commitSourceFolder;
    }
}
