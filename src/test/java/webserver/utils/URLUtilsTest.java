package webserver.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class URLUtilsTest {

    @Test
    @DisplayName("파일 확장자를 구할 수 있다")
    void getExtension() {
        final String path = "/registration/index.html";
        final String path2 = "bootstrap.min.css";
        final String path3 = "/main";

        final String pathResult = "html";
        final String pathResult2 = "css";
        final String pathResult3 = "";

        assertThat(URLUtils.getExtension(path)).isEqualTo(pathResult);
        assertThat(URLUtils.getExtension(path2)).isEqualTo(pathResult2);
        assertThat(URLUtils.getExtension(path3)).isEqualTo(pathResult3);
    }

    @Test
    @DisplayName("URL에서 쿼리스트링이 존재하는지 확인할 수 있다")
    void isQueryExists() {
        final String requestUrl = "/user/create?username=hello";
        final String requestUrl2 = "/user/create";
        assertThat(URLUtils.isQueryExists(requestUrl)).isTrue();
        assertThat(URLUtils.isQueryExists(requestUrl2)).isFalse();
    }

    @Test
    @DisplayName("URL에서 path를 추출할 수 있다")
    void getPath() {
        final String requestUrl = "/user/create?username=hello";
        final String path = "/user/create";
        assertThat(URLUtils.getPath(requestUrl)).isEqualTo(path);
        assertThat(URLUtils.getPath(path)).isEqualTo(path);
    }

    @Test
    @DisplayName("URL에서 쿼리스트링을 추출할 수 있다")
    void getQuery() {
        final String requestUrl = "/user/create?username=hello";
        final String requestUrl2 = "/user/create";
        final String query = "username=hello";
        assertThat(URLUtils.getQuery(requestUrl)).isEqualTo(query);
        assertThat(URLUtils.getQuery(requestUrl2)).isEmpty();
    }

    @Test
    @DisplayName("쿼리 문자열을 Map 형태로 변환할 수 있다. 만약 쿼리 문자열이 비어있거나 잘못되었으면 예외를 발생시킨다")
    void configureQuery() {
        final String queryString = "username=hello";
        Map<String, String> queryMap = Map.of("username", "hello");
        assertThat(URLUtils.configureQuery(queryString))
                .isEqualTo(queryMap);
        assertThatThrownBy(() -> URLUtils.configureQuery(""))
                .isInstanceOf(RuntimeException.class);
    }
}