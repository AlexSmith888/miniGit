package infrastructure.encryption;

import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class PathCipher implements PathsEncryption{
    private String data;
    private String hash;

    @Override
    public void process(Path path) {
        data = path.toString()
                + System.currentTimeMillis();
        hash = returnSHA1();
    }

    private String returnSHA1() {
        byte[] digest = null;
        try {
            MessageDigest sh1 = MessageDigest.getInstance("SHA-1");
            digest = sh1.digest(getData().getBytes());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Algo have not been found");
        }
        return HexFormat.of().formatHex(digest).substring(11);
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public String getShortHash() {
        return hash.substring(0, 7);
    }

    @Override
    public String getLongHash() {
        return hash.substring(7);
    }

    @Override
    public String getHash() {
        return hash;
    }
}
