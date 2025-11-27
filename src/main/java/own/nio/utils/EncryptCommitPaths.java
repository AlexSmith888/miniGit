package own.nio.utils;

import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class EncryptCommitPaths {
    static private String data;
    private static String hash;
    static public void process(Path path) {
        data = path.toString()
                + System.currentTimeMillis();
        hash = returnSHA1();
    }
    static private String returnSHA1() {
        byte[] digest = null;
        try {
            MessageDigest sh1 = MessageDigest.getInstance("SHA-1");
            digest = sh1.digest(getData().getBytes());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Algo have not been found");
        }
        return HexFormat.of().formatHex(digest).substring(11);
    }

    static private String getData() {
        return data;
    }
    public static String getShortHash(){
        return hash.substring(0, 7);
    }
    public static String getLongHash(){
        return hash.substring(7);
    }
    public static String getHash(){
        return hash;
    }
}
