package exception;

import webserver.type.HttpStatusCode;
import static webserver.type.HttpStatusCode.*;

public enum CustomErrorType {
    // 1XXX 서버 요청, 응답 오류
    PATH_NOT_FOUND(NOT_FOUND, 1000, "존재하지 않는 경로입니다."),
    SERVER_ERROR(INTERNAL_SERVER_ERROR, 1001, "서버 내부에 오류가 발생했습니다."),
    MALFORMED_URL_FORMAT(BAD_REQUEST, 1002, "잘못된 URL 형식입니다."),
    MALFORMED_BODY_FORMAT(BAD_REQUEST, 1003, "잘못된 Request Body 형식입니다."),
    INVALID_VALUE(BAD_REQUEST, 1004, "요청 값이 유효하지 않습니다."),

    // 2XXX 사용자 관련 오류
    USER_ALREADY_EXISTS(CONFLICT, 2000, "이미 등록된 사용자입니다."),
    ;

    private final HttpStatusCode httpStatusCode;
    private final int code;
    private final String errorMessage;

    CustomErrorType(HttpStatusCode httpStatusCode, int code, String errorMessage) {
        this.httpStatusCode = httpStatusCode;
        this.code = code;
        this.errorMessage = errorMessage;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    public int getCode() {
        return code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
