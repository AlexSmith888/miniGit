package infrastructure.entities;

import java.nio.file.Path;

public interface RecursiveWorker {
    public void setSource(Path source);
    public void setTarget(Path target);

    public void addToExcludedList(Path path);

    public void truncateExcludedList();
}
