package app.state;

import domain.entities.MIniGitRepository;
import domain.entities.MiniGitEntity;

import java.io.IOException;

public interface RequestState {

    void saveCurrentState(MIniGitRepository gitRepo) throws IOException;

    void recoverPreviousState(MIniGitRepository gitRepo) throws IOException;
    void clean(MIniGitRepository gitRepo) throws IOException;
}
