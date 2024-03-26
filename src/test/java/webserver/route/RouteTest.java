package webserver.route;

import db.Database;
import exception.server.PathNotFoundException;
import model.User;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import webserver.common.HttpBody;
import webserver.common.HttpHeader;
import webserver.common.HttpRequestLine;
import webserver.request.HttpRequest;
import webserver.utils.RequestParser;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

class RouteTest {
    private final SoftAssertions softAssertions = new SoftAssertions();

    private HttpRequest wrongDynamicRequest;
    private HttpRequest wrongStaticRequest;

    @BeforeEach
    @AfterEach
    @SuppressWarnings("unchecked")
    void clear() throws NoSuchFieldException, IllegalAccessException {
        Field userMap = Database.class.getDeclaredField("users");
        userMap.setAccessible(true);
        Map<String, User> users = (Map<String, User>) userMap.get(Database.class);
        users.clear();
    }

    @BeforeEach
    void setUp() {
        HttpRequestLine dynamicRequestLine = new HttpRequestLine("GET /user/create HTTP/1.1");
        HttpHeader dynamicHttpHeader = new HttpHeader(RequestParser.parseHeader(
                List.of("Content-Type: text/plain", "Content-Length: 65")));
        HttpBody dynamicHttpBody = new HttpBody("userId=hello&email=hello@gmail.com&nickname=hello&password=1234".getBytes());
        wrongDynamicRequest = new HttpRequest(dynamicRequestLine, dynamicHttpHeader, dynamicHttpBody);

        HttpRequestLine staticRequestLine = new HttpRequestLine("GET /regis HTTP/1.1");
        HttpHeader staticRequestHeader = new HttpHeader();
        HttpBody staticRequestBody = new HttpBody();
        wrongStaticRequest = new HttpRequest(staticRequestLine, staticRequestHeader, staticRequestBody);
    }

    @Test
    void routeFail() {
        softAssertions.assertThatThrownBy(() -> Route.getInstance().route(wrongDynamicRequest))
                .isInstanceOf(PathNotFoundException.class);
        softAssertions.assertThatThrownBy(() -> Route.getInstance().route(wrongStaticRequest))
                .isInstanceOf(PathNotFoundException.class);
    }
}
