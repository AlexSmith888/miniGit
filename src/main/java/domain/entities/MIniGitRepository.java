package domain.entities;

import java.nio.file.Path;

public class MIniGitRepository implements MiniGitEntity {
    private final Path sourceDir;
    private final Path sourceGitDir;
    private final Path sourceGitTempDir;
    private final Path sourceGitCommitDir;
    private final String [] sourceRawData;
    private final String command;
    private final String commitMessage;
    private final String metaFile;
    private final String short1;
    private final String short2;

    private MIniGitRepository(Builder builder) {
        this.sourceDir = builder.sourceDir;
        this.sourceGitDir = builder.sourceGitDir;
        this.sourceGitTempDir = builder.sourceGitTempDir;
        this.sourceGitCommitDir = builder.sourceGitCommitDir;
        this.sourceRawData = builder.sourceRawData;
        this.command = builder.command;
        this.commitMessage = builder.commitMessage;
        this.metaFile = builder.metaFile;
        this.short1 = builder.short1;
        this.short2 = builder.short2;
    }

    static public class Builder {

        private Path sourceDir;
        private Path sourceGitDir;
        private Path sourceGitTempDir;
        private Path sourceGitCommitDir;
        private String [] sourceRawData;
        private String command;
        private String commitMessage;
        private String metaFile;
        private String short1;
        private String short2;

        public Builder withRawData (String[] arr){
            this.sourceRawData = arr;
            return this;
        }

        public Builder withCommand (){
            this.command = sourceRawData[0];
            return this;
        }

        public Builder withSourceDir (){
            sourceDir = Path.of(sourceRawData[1]);
            return this;
        }

        public Builder withSourceGitDir (){
            sourceGitDir = Path.of(sourceRawData[1] + "/miniGit");
            return this;
        }

        public Builder withSourceGitTempDir (){
            sourceGitTempDir = Path.of(sourceRawData[1] + "/miniGit" + "/temp");
            return this;
        }

        public Builder withSourceGitCommitDir (){
            sourceGitCommitDir = Path.of(sourceRawData[1] + "/miniGit" + "/commits");
            return this;
        }
        public Builder withCommitMessage (){
            this.commitMessage = sourceRawData[2];
            return this;
        }
        public Builder withMetaFile (){
            this.metaFile = "meta.json";
            return this;
        }
        public Builder withCommitName1 (){
            this.short1 = sourceRawData[2];
            return this;
        }
        public Builder withCommitName2 (){
            this.short2 = sourceRawData[3];
            return this;
        }

        public MiniGitEntity build(){
            return new MIniGitRepository(this);
        }
    }

    @Override
    public Path returnSourceDir() {
        return sourceDir;
    }

    @Override
    public Path returnSourceGitDir() {
        return sourceGitDir;
    }

    @Override
    public Path returnSourceGitTempDir() {
        return sourceGitTempDir;
    }

    @Override
    public Path returnSourceGitCommitDir() {
        return sourceGitCommitDir;
    }

    @Override
    public String[] returnSourceRawRequest() {
        return sourceRawData;
    }

    @Override
    public String returnCommand() {
        return command;
    }
    public String returnCommitMessage() {
        return commitMessage;
    }
    public String returnMetaFile() {return metaFile;}
    public String returnCommitShort1() {return short1;}
    public String returnCommitShort2() {return short2;}
}
