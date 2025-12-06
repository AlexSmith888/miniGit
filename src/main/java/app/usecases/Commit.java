package app.usecases;

import app.state.StateContract;
import domain.services.Request;
import domain.entities.MIniGitRepository;
import infrastructure.storage.JsonContract;
import infrastructure.storage.JsonEntity;

import java.io.IOException;
import java.nio.file.Path;

public class Commit implements Request, JsonContract, StateContract {

    @Override
    public void execute(MIniGitRepository entity) throws IOException {
        //setRootRepository(entity);
        //entity.returnState().saveCurrentState();
        //System.out.println("Root --- >" + entity.returnState().getRoot());;
        Path mainCommitDirectory ;
        Path mainDataCommitDirectory;
        try {
            entity.returnCipher().process(entity.returnSourceDir());
            mainCommitDirectory = entity.returnSourceGitCommitDir().resolve(
                    entity.returnCipher().getShortHash());
            mainDataCommitDirectory = mainCommitDirectory.resolve(
                    entity.returnCipher().getLongHash());
            entity.returnFileSystem().createDir(mainCommitDirectory);
            entity.returnFileSystem().createDir(mainDataCommitDirectory);
        } catch (IOException e) {
            //entity.returnState().recoverPreviousState();
            System.out.println("The issue while creating commit directories");
            throw e;
        }

        try {
            entity.returnCopier().setSource(entity.returnSourceGitTempDir());
            entity.returnCopier().setTarget(mainDataCommitDirectory);
            entity.returnFileSystem().copyRecursively(entity.returnSourceGitTempDir()
                    , entity.returnCopier());
        } catch (IOException e) {
            System.out.println("Copying files to commit directories failed");
            System.out.println(e.getMessage());
            //entity.returnState().recoverPreviousState();
            throw e;
        }

        try {

            setShortFolderIdentifier(entity.returnJson(), entity.returnCipher().getShortHash());
            setLongFolderIdentifier(entity.returnJson(), entity.returnCipher().getLongHash());
            setFullFolderIdentifier(entity.returnJson(), entity.returnCipher().getHash());
            setEventTimestamp(entity.returnJson(), String.valueOf(System.currentTimeMillis()));
            setCommitSourceFolder(entity.returnJson(), entity.returnSourceDir().toString());
            setCommitMessage(entity.returnJson(), entity.returnCommitMessage());

            entity.returnFileSystem().writeJsonToTheDisk(
                    Path.of(mainCommitDirectory + "/" + entity.returnMetaFile())
                    , entity.returnJson());

            entity.returnCommitsCache()
                    .addCommitToTree(entity.returnSourceDir(), entity.returnCipher().getShortHash());

        } catch (IOException e) {
            System.out.println("Failed to create a Json file");
            //entity.returnState().recoverPreviousState();
            throw e;
        }
        //entity.returnState().clean();
    }

    @Override
    public void setShortFolderIdentifier(JsonEntity entity, String value) {
        entity.setShortFolderIdentifier(value);
    }

    @Override
    public void setLongFolderIdentifier(JsonEntity entity, String value) {
        entity.setlongFolderIdentifier(value);
    }

    @Override
    public void setFullFolderIdentifier(JsonEntity entity, String value) {
        entity.setfullFolderIdentifier(value);
    }

    @Override
    public void setEventTimestamp(JsonEntity entity, String value) {
        entity.seteventTimestamp(value);
    }

    @Override
    public void setCommitMessage(JsonEntity entity, String value) {
        entity.setCommitMessage(value);
    }

    @Override
    public void setCommitSourceFolder(JsonEntity entity, String value) {
        entity.setcommitSourceFolder(value);
    }

    @Override
    public void setRootRepository(MIniGitRepository repo) {
        repo.returnState().setRoot(repo.returnSourceDir());
    }
}
