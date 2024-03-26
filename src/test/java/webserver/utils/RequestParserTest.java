package webserver.utils;

import exception.server.MalformedBodyFormatException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RequestParserTest {
    private final SoftAssertions softAssertions = new SoftAssertions();

    @Test
    @DisplayName("Optional White Space가 있어도 헤더가 잘 파싱되어야 한다.")
    void parseHeader() {
        List<String> requestHeader = List.of("Content-Type: application/json", "Content-Length :16");
        Map<String, List<String>> parsed = Map.of("Content-Type", List.of("application/json"),
                "Content-Length", List.of("16"));

        assertThat(RequestParser.parseHeader(requestHeader)).isEqualTo(parsed);
    }

    @Test
    @DisplayName("바디를 파싱할 수 있고, 바디가 유효하지 않으면 예외를 발생시켜야 한다")
    void parseBody() {
        String wrongBodyFormat = """
                {
                    "userId":"hello",
                    "nickname":"hello"
                }""";

        String rightBodyFormat = "userId=hello&email=hello@gmail.com&nickname=hello&password=1234";

        softAssertions.assertThatThrownBy(() -> RequestParser.parseBody(wrongBodyFormat.getBytes()))
                .isInstanceOf(MalformedBodyFormatException.class);

        softAssertions.assertThat(RequestParser.parseBody(rightBodyFormat.getBytes()))
                .isEqualTo(Map.of("userId", "hello", "email", "hello@gmail.com", "nickname", "hello", "password", "1234"));

        softAssertions.assertAll();
    }
}