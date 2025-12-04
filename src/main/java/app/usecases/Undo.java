package app.usecases;

import domain.services.Request;
import domain.entities.MIniGitRepository;

import java.io.IOException;

public class Undo implements Request {
    @Override
    public void execute(MIniGitRepository entity) throws IOException {
        try {
            entity.returnEraser().setSource(entity.returnSourceGitDir());
            entity.returnFileSystem().eraseRecursively(
                    entity.returnSourceGitDir(), entity.returnEraser());

            entity.returnRepos().returnCachedDirectories()
                    .remove(entity.returnSourceDir());
            entity.returnCommitsCache()
                    .removeCommitsTree(entity.returnSourceDir());
        } catch (IOException e) {
            System.out.println("Impossible to delete miniGit folder");
            System.out.println(e.getMessage());
            throw e;
        }
    }
}
