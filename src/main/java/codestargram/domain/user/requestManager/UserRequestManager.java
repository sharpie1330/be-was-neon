package codestargram.domain.user.requestManager;

import codestargram.domain.user.data.UserSaveData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.annotation.RequestBody;
import webserver.annotation.Valid;
import webserver.response.HttpResponse;
import webserver.annotation.RequestMapping;
import codestargram.domain.user.handler.UserHandler;
import webserver.type.HttpMethod;

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
}
