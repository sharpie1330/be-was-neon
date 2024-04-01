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

        // redirect 응답 전송
        return HttpResponse
                .found(cookie.get("redirectUrl") != null ? cookie.get("redirectUrl") : "/registration/welcome.html")
                .header(HttpHeader.SET_COOKIE, createCookie("SID", userSaveData.getUserId(), 3600, "/").toString())
                .header(HttpHeader.SET_COOKIE + 1, createCookie("redirectUrl", "", 0, "/").toString())
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
                .header(HttpHeader.SET_COOKIE, createCookie("SID", userLoginData.getUserId(), 3600, "/").toString())
                .header(HttpHeader.SET_COOKIE + 1, createCookie("redirectUrl", "", 0, "/").toString())
                .build();
    }

    @RequestMapping(path = "/logout", method = HttpMethod.POST)
    public HttpResponse logout(@Authorize Cookie cookie) {
        String sid = cookie.get("SID");
        String userId = Session.getUserId(sid);
        userHandler.logout(userId);
        Session.expireSession(sid);

        return HttpResponse
                .found("/")
                .header(HttpHeader.SET_COOKIE, createCookie("SID", "", 0, "/").toString())
                .build();
    }

    private Cookie createCookie(String cookieKey, String cookieValue, int maxAge, String path) {
        Cookie cookie = new Cookie();
        cookie.setCookie(cookieKey, cookieValue);
        cookie.setMaxAge(maxAge);
        cookie.setPath(path);

        return cookie;
    }
}
