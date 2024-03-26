package webserver.route.user.requestManager;

import exception.CustomException;
import exception.common.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.common.KeyValueHttpBody;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;
import webserver.route.requestMapping.RequestMapping;
import webserver.route.user.data.UserSaveData;
import webserver.route.user.handler.UserHandler;
import webserver.type.HttpMethod;
import webserver.type.HttpStatusCode;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static webserver.utils.PropertyUtils.loadProperties;

public class UserRequestManager {
    private static final Logger logger = LoggerFactory.getLogger(UserRequestManager.class);
    private static final String CHARSET = loadProperties().getProperty("charset");

    private final UserHandler userHandler;

    public UserRequestManager() {
        userHandler = new UserHandler();
    }

    @RequestMapping(path = "/user/create", method = HttpMethod.POST)
    public HttpResponse createUser(HttpRequest httpRequest) {
        // body 얻어서 keyValueMap으로 변경
        KeyValueHttpBody body = KeyValueHttpBody.of(httpRequest.getBody());

        // 저장할 정보 객체 생성
        UserSaveData userSaveData = createSaveData(body);

        // 쿼리 파리미터 유효성 확인
        if (!validateUserCreateParam(userSaveData)) {
            throw new BadRequestException();
        }

        // 유저 생성
        userHandler.userCreate(userSaveData);

        // redirect 응답 전송
        final String welcomePage = "/registration/welcome.html";
        return HttpResponse
                .found(httpRequest.getRequestLine().getVersion(), welcomePage)
                .build();
    }

    private UserSaveData createSaveData(KeyValueHttpBody body) {
        // 디코딩
        try {
            String userId = URLDecoder.decode(body.get("userId"), CHARSET);
            String password = URLDecoder.decode(body.get("password"), CHARSET);
            String nickname = URLDecoder.decode(body.get("nickname"), CHARSET);
            String email = URLDecoder.decode(body.get("email"), CHARSET);
            return new UserSaveData(userId, password, nickname, email);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
            throw new CustomException(HttpStatusCode.INTERNAL_SERVER_ERROR);
        }
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