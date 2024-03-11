package utils;

public class StringUtils {
    private static final String NEW_LINE = "\r\n";

    private StringUtils() {

    }

    public static String appendNewLine(String origin) {
        return origin + NEW_LINE;
    }
}
