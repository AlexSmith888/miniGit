package own.nio.utils;

public class InputProcessing {
    static public String [] returnInitInput(String values) {
        String [] arr = values.toLowerCase().trim().split(" ");
        return arr;
    }
}
