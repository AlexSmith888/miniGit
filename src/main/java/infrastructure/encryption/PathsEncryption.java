package infrastructure.encryption;

import java.nio.file.Path;

public interface PathsEncryption {
    public String getData();

    public String getShortHash();

    public String getLongHash();

    public String getHash();

    public void process(Path path);
}
