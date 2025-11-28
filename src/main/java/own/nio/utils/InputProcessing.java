package own.nio.utils;

public class InputProcessing {
    static public String [] returnInitInput(String values) {
        if (values.contains("\"")) {
            String third = values.substring(values.indexOf("\""));
            String[] vals = values.split(" ");
            return new String[]{vals[0], vals[1], third};
        }
        return values.toLowerCase().trim().split(" ");
    }
}
