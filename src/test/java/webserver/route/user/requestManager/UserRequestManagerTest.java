package webserver.route.user.requestManager;

import db.Database;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.common.HttpBody;
import webserver.common.HttpHeader;
import webserver.common.HttpRequestLine;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;
import webserver.type.HttpStatusCode;
import webserver.utils.RequestParser;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class UserRequestManagerTest {
    private UserRequestManager userRequestManager;

    @BeforeEach
    void setUp() {
        userRequestManager = new UserRequestManager();
    }

    @BeforeEach
    @AfterEach
    @SuppressWarnings("unchecked")
    void clear() throws NoSuchFieldException, IllegalAccessException {
        Field userMap = Database.class.getDeclaredField("users");
        userMap.setAccessible(true);
        Map<String, User> users = (Map<String, User>) userMap.get(Database.class);
        users.clear();
    }

    @Test
    @DisplayName("유저를 저장할 수 있다")
    void createUser() {
        HttpRequestLine requestLine = new HttpRequestLine("POST /user/create HTTP/1.1");
        HttpHeader httpHeader = new HttpHeader(RequestParser.parseHeader(
                List.of("Content-Type: text/plain", "Content-Length: 65")));
        HttpBody httpBody = new HttpBody("userId=hello&email=hello%40gmail.com&nickname=hello&password=1234");
        HttpRequest httpRequest = new HttpRequest(requestLine, httpHeader, httpBody);
        HttpResponse httpResponse = userRequestManager.createUser(httpRequest);

        Optional<User> hello = Database.findUserById("hello");

        assertThat(hello.isPresent()).isTrue();
        assertThat(hello.get().getUserId()).isEqualTo("hello");
        assertThat(hello.get().getName()).isEqualTo("hello");
        assertThat(hello.get().getEmail()).isEqualTo("hello@gmail.com");
        assertThat(hello.get().getPassword()).isEqualTo("1234");

        assertThat(httpResponse.getHttpStatusCode()).isEqualTo(HttpStatusCode.FOUND);
        assertThat(httpResponse.getHeaders().getLocation()).isEqualTo("/registration/welcome.html");
    }
}