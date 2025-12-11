package utils;

public class CLiParser {
    static public String [] returnInitInput(String values) {
        values = values.trim();
        if (values.contains("\"")) {
            String third = values.substring(values.indexOf("\""));
            String[] vals = values.split(" ");
            return new String[]{vals[0], vals[1], third};
        }
        return values.split(" ");
    }
}
