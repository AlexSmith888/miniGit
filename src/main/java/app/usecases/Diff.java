package app.usecases;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import domain.services.Request;
import domain.entities.MIniGitRepository;

import java.io.IOException;
import java.nio.file.Path;

public class Diff implements Request {
    @Override
    public void execute(MIniGitRepository entity) throws IOException {

        try {
            Path dir1 = Path.of(entity.returnSourceGitCommitDir()
                    + "/" + entity.returnCommitShort1());
            Path dir2 = Path.of(entity.returnSourceGitCommitDir()
                    + "/" + entity.returnCommitShort2());

            dir1 =
                    entity.returnFileSystem().returnfullPath(
                            dir1, entity.returnMetaFile());
            dir2 =
                    entity.returnFileSystem().returnfullPath(
                            dir2, entity.returnMetaFile());

            entity.returnViewer().setSource(dir1);
            entity.returnViewer().setTarget(dir2);
            entity.returnFileSystem().viewDifference(dir1, entity.returnViewer());
        } catch (IOException e) {
            System.out.println("Failed to complete diff request ");
            throw e;
        }
    }
}
