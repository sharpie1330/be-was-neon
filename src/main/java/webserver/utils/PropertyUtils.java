package webserver.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyUtils {

    // TODO: build.gradle에서 처리할 수 있는지 고민해볼것
    private static final String PROPERTY_FILEPATH = "./src/main/java/webserver/config.properties";

    private PropertyUtils() {

    }

    public static Properties loadProperties() {
        Properties properties = new Properties();
        File file = new File(PROPERTY_FILEPATH);

        try (FileInputStream fis = new FileInputStream(file)) {
            properties.load(fis);
        } catch (IOException e) {
            e.fillInStackTrace();
        }

        return properties;
    }

    public static int loadPortFromProperties() {
        return Integer.parseInt(loadProperties().getProperty("port"));
    }

    public static String loadStaticSourcePathFromProperties() {
        return loadProperties().getProperty("staticSourcePath");
    }
}
