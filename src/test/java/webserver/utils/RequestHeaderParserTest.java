package webserver.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHeaderParserTest {

    @Test
    @DisplayName("Optional White Space가 있어도 헤더가 잘 파싱되어야 한다.")
    void parse() {
        List<String> requestHeader = List.of("Content-Type: application/json", "Content-Length :16");
        Map<String, List<String>> parsed = Map.of("Content-Type", List.of("application/json"),
                "Content-Length", List.of("16"));

        assertThat(RequestHeaderParser.parse(requestHeader)).isEqualTo(parsed);
    }
}