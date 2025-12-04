package domain.entities;

import infrastructure.entities.CommitsCacheGateway;
import infrastructure.entities.FileSystemGateway;
import infrastructure.entities.RepositoriesGateway;
import infrastructure.filesystem.Cleaner;
import infrastructure.filesystem.Copier;
import infrastructure.filesystem.Eraser;
import infrastructure.filesystem.Viewer;

import javax.swing.text.View;
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
    private final CommitsCacheGateway commitsGW;
    private final RepositoriesGateway repos;
    private final FileSystemGateway fsGateway;
    private final Copier copier;
    private final Cleaner cleaner;
    private final Eraser eraser;
    private final Viewer viewer;

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
        this.commitsGW = builder.commitsGW;
        this.repos = builder.repos;
        this.fsGateway = builder.fsGateway;
        this.copier = builder.copier;
        this.cleaner = builder.cleaner;
        this.eraser = builder.eraser;
        this.viewer = builder.viewer;
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
        private CommitsCacheGateway commitsGW;
        private RepositoriesGateway repos;
        private FileSystemGateway fsGateway;
        private Copier copier;
        private Cleaner cleaner;
        private Eraser eraser;
        private Viewer viewer;

        public Builder withViewer (Viewer viewer){
            this.viewer = viewer;
            return this;
        }

        public Builder withEraser (Eraser eraser){
            this.eraser = eraser;
            return this;
        }

        public Builder withCleaner (Cleaner cleaner){
            this.cleaner = cleaner;
            return this;
        }

        public Builder withCopier (Copier copier){
            this.copier = copier;
            return this;
        }

        public Builder withFileSystem (FileSystemGateway gw){
            this.fsGateway = gw;
            return this;
        }

        public Builder withRepositories (RepositoriesGateway repos){
            this.repos = repos;
            return this;
        }

        public Builder withCommitsCache (CommitsCacheGateway gw){
            this.commitsGW = gw;
            return this;
        }

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
    public CommitsCacheGateway returnCommitsCache() {return commitsGW;}
    public RepositoriesGateway returnRepos() {return repos;}
    public FileSystemGateway returnFileSystem() {return fsGateway;}
    public Copier returnCopier() {return copier;}
    public Cleaner returnCleaner() {return cleaner;}
    public Eraser returnEraser() {return eraser;}
    public Viewer returnViewer() {return viewer;}
}
