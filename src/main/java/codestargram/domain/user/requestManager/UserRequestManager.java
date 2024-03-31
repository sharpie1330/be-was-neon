package codestargram.domain.user.requestManager;

import codestargram.domain.user.data.UserLoginData;
import codestargram.domain.user.data.UserSaveData;
import codestargram.exception.user.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.annotation.RequestBody;
import webserver.annotation.Valid;
import webserver.exception.server.UnauthorizedException;
import webserver.http.type.HttpHeader;
import webserver.http.type.HttpResponse;
import webserver.annotation.RequestMapping;
import codestargram.domain.user.handler.UserHandler;
import webserver.http.type.HttpMethod;
import webserver.session.Cookie;
import webserver.session.Session;

import java.util.UUID;

public class UserRequestManager {
    private static final Logger logger = LoggerFactory.getLogger(UserRequestManager.class);

    private final UserHandler userHandler;

    public UserRequestManager() {
        userHandler = new UserHandler();
    }

    @RequestMapping(path = "/user/create", method = HttpMethod.POST)
    public HttpResponse createUser(@Valid @RequestBody UserSaveData userSaveData) {
        // 유저 생성
        userHandler.userCreate(userSaveData);

        // redirect 응답 전송
        final String welcomePage = "/registration/welcome.html";
        return HttpResponse
                .found(welcomePage)
                .build();
    }

    @RequestMapping(path = "/login", method = HttpMethod.POST)
    public HttpResponse login(@Valid @RequestBody UserLoginData userLoginData) {
        try {
            userHandler.login(userLoginData);
        } catch (UserNotFoundException | UnauthorizedException e) {
            return HttpResponse
                    .found("/login/login_failed.html")
                    .build();
        }

        String sessionId = Session.setSession(userLoginData.getUserId());
        Cookie cookie = new Cookie();
        cookie.setSID(sessionId);
        cookie.setPath("/");
        cookie.setMaxAge(3600);

        return HttpResponse
                .found("/main")
                .header(HttpHeader.SET_COOKIE, cookie.toString())
                .build();
    }
}
