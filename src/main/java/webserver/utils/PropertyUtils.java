package webserver.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyUtils {

    private PropertyUtils() {

    }

    public static Properties loadProperties(String PROPERTY_FILEPATH) {
        Properties properties = new Properties();
        File file = new File(PROPERTY_FILEPATH);

        try (FileInputStream fis = new FileInputStream(file)) {
            properties.load(fis);
        } catch (IOException e) {
            e.fillInStackTrace();
        }

        return properties;
    }
}
