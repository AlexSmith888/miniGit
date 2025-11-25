package own.nio.utils;

import java.nio.file.Path;
import java.util.List;

public class CachedDirectories {
    public static List<Path> lst;
    public CachedDirectories(List<Path> lst){
        this.lst = lst;
    }
    static public List<Path> returnDirectories(){
        return lst;
    }
}
