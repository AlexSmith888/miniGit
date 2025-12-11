package app.usecases;

import domain.services.Request;
import domain.entities.MIniGitRepository;

import java.io.IOException;

public class Undo implements Request {
    /*
    Removes miniGit commit tree directories structure under user's repository
    stops tracking a repository
    * */
    private void recoverAndClean(MIniGitRepository entity) throws IOException{
        entity.returnState().recoverPreviousState(entity);
        entity.returnState().clean(entity);
    }
    @Override
    public void execute(MIniGitRepository entity) throws IOException {
        entity.returnState().saveCurrentState(entity);
        try {
            entity.returnEraser().setSource(entity.returnSourceGitDir());
            entity.returnFileSystem().eraseRecursively(
                    entity.returnSourceGitDir(), entity.returnEraser());

            entity.returnRepos().returnCachedDirectories()
                    .remove(entity.returnSourceDir());
            entity.returnCommitsCache()
                    .removeCommitsTree(entity.returnSourceDir());
        } catch (IOException e) {
            entity.returnLogger().error("Impossible to delete miniGit folder");
            entity.returnLogger().error(e.getMessage());
            recoverAndClean(entity);
            throw e;
        }
        entity.returnState().clean(entity);
    }
}
