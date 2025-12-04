package app.usecases;

import domain.services.Request;
import domain.entities.MIniGitRepository;

import java.io.IOException;

public class Track implements Request {
    @Override
    public void execute(MIniGitRepository entity) throws IOException {
        try {
            entity.returnCleaner().setSource(entity.returnSourceGitTempDir());
            entity.returnFileSystem().deleteRecursively(entity.returnSourceGitTempDir()
                    ,entity.returnCleaner());

            entity.returnCopier().setSource(entity.returnSourceDir());
            entity.returnCopier().setTarget(entity.returnSourceGitTempDir());
            entity.returnCopier().addToExcludedList(entity.returnSourceGitDir());
            entity.returnFileSystem().copyRecursively(entity.returnSourceDir()
                    ,entity.returnCopier());
        } catch (IOException e) {
            System.out.println("Impossible to track changes");
            System.out.println(e.getMessage());
            throw e;
        }
    }
}
