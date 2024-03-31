package codestargram.domain.user.requestManager;

import codestargram.domain.user.data.UserSaveData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.annotation.RequestBody;
import webserver.annotation.Valid;
import webserver.exception.server.BadRequestException;
import webserver.response.HttpResponse;
import webserver.annotation.RequestMapping;
import codestargram.domain.user.handler.UserHandler;
import webserver.type.HttpMethod;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    // 유저 생성 파라미터 유효성 확인
    private boolean validateUserCreateParam(UserSaveData userSaveData) {
        String userId = userSaveData.getUserId();
        String password = userSaveData.getPassword();
        String nickname = userSaveData.getNickname();
        String email = userSaveData.getEmail();

        // null 체크
        if (userId == null || password == null || nickname == null || email == null) {
            return false;
        }

        // 이메일 유효성 확인
        final String emailRegex = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            return false;
        }

        // 빈 값 확인
        return !userId.isBlank() && !password.isBlank() && !nickname.isBlank() && !email.isBlank();
    }
}
