package codestargram.domain.user.requestManager;

import codestargram.domain.user.data.UserLoginData;
import codestargram.domain.user.data.UserSaveData;
import codestargram.exception.user.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.annotation.Authorize;
import webserver.annotation.RequestBody;
import webserver.annotation.Valid;
import webserver.exception.server.UnAuthorizedException;
import webserver.http.type.HttpHeader;
import webserver.http.type.HttpResponse;
import webserver.annotation.RequestMapping;
import codestargram.domain.user.handler.UserHandler;
import webserver.http.type.HttpMethod;
import webserver.session.Cookie;
import webserver.session.Session;

import java.util.List;

public class UserRequestManager {
    private static final Logger logger = LoggerFactory.getLogger(UserRequestManager.class);

    private final UserHandler userHandler;

    public UserRequestManager() {
        userHandler = new UserHandler();
    }

    @RequestMapping(path = "/user/create", method = HttpMethod.POST)
    public HttpResponse createUser(@Authorize Cookie cookie, @Valid @RequestBody UserSaveData userSaveData) {
        // 유저 생성
        userHandler.userCreate(userSaveData);

        logger.debug(cookie.get("redirectUrl") != null ? cookie.get("redirectUrl") : "/registration/welcome.html");

        HttpResponse build = HttpResponse
                .found(cookie.get("redirectUrl") != null ? cookie.get("redirectUrl") : "/registration/welcome.html")
                .header(HttpHeader.SET_COOKIE, createCookie(userSaveData.getUserId()).toString())
                .header(HttpHeader.SET_COOKIE + 1, "redirectUrl=;Max-Age=0")
                .build();

        List<String> list = build.getHeaders().get(HttpHeader.SET_COOKIE);
        List<String> list2 = build.getHeaders().get(HttpHeader.SET_COOKIE + 1);

        logger.debug(list.toString());
        logger.debug(list2.toString());

        // redirect 응답 전송
        return HttpResponse
                .found(cookie.get("redirectUrl") != null ? cookie.get("redirectUrl") : "/registration/welcome.html")
                .header(HttpHeader.SET_COOKIE, createCookie(userSaveData.getUserId()).toString())
                .header(HttpHeader.SET_COOKIE + 1, "redirectUrl=;Max-Age=0")
                .build();
    }

    @RequestMapping(path = "/login", method = HttpMethod.POST)
    public HttpResponse login(@Authorize Cookie cookie, @Valid @RequestBody UserLoginData userLoginData) {
        try {
            userHandler.login(userLoginData);
        } catch (UserNotFoundException | UnAuthorizedException e) {
            return HttpResponse
                    .found("/login/login_failed.html")
                    .build();
        }

        return HttpResponse
                .found(cookie.get("redirectUrl") != null ? cookie.get("redirectUrl") : "/main")
                .header(HttpHeader.SET_COOKIE, createCookie(userLoginData.getUserId()).toString())
                .header(HttpHeader.SET_COOKIE + 1, "redirectUrl=;Max-Age=0")
                .build();
    }

    private Cookie createCookie(String userId) {
        Cookie cookie = new Cookie();
        cookie.setSID(Session.setSession(userId));
        cookie.setPath("/");
        cookie.setMaxAge(3600);

        return cookie;
    }
}
