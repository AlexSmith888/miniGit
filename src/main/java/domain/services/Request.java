package domain.services;

import domain.entities.MIniGitRepository;

import java.io.IOException;

public interface Request {
    public void execute(MIniGitRepository entity) throws IOException;
}
