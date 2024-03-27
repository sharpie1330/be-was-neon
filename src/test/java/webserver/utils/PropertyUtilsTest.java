package webserver.utils;

import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class PropertyUtilsTest {
    private final Properties properties = PropertyUtils.loadProperties();

    @Test
    void loadPortFromProperties() {
        assertThat(properties.getProperty("port")).isEqualTo(String.valueOf(8080));
    }

    @Test
    void loadStaticSourcePathFromProperties() {
        assertThat(properties.getProperty("staticSourcePath")).isEqualTo("./src/main/resources/static");
    }
}