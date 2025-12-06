package app.state;

import domain.entities.MIniGitRepository;

public interface StateContract {
    void setRootRepository(MIniGitRepository repo);
}
