package app.usecases;

import domain.services.Request;
import domain.entities.MIniGitRepository;

import java.io.IOException;

public class Init implements Request {
    @Override
    public void execute(MIniGitRepository entity) throws IOException {
        try {
            entity.returnFileSystem().createDir(entity.returnSourceGitDir());
            entity.returnFileSystem().createDir(entity.returnSourceGitTempDir());
            entity.returnFileSystem().createDir(entity.returnSourceGitCommitDir());

            if (!entity.returnRepos().returnCachedDirectories()
                    .contains(entity.returnSourceDir())) {
                entity.returnRepos().returnCachedDirectories()
                        .add(entity.returnSourceDir());
            }

            entity.returnCopier().setSource(entity.returnSourceDir());
            entity.returnCopier().setTarget(entity.returnSourceGitTempDir());
            entity.returnCopier().addToExcludedList(entity.returnSourceGitDir());
            entity.returnFileSystem().copyRecursively(entity.returnSourceDir()
                    ,entity.returnCopier());

        } catch (IOException e) {
            System.out.println("Impossible to create a miniGit repository");
            System.out.println(e.getMessage());
            throw e;
        }
    }
}
