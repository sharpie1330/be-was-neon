package exception;

import webserver.type.HttpStatusCode;
import static webserver.type.HttpStatusCode.*;

public enum CustomErrorType {
    PATH_NOT_FOUND(NOT_FOUND, 1000, "존재하지 않는 경로입니다."),
    SERVER_ERROR(INTERNAL_SERVER_ERROR, 1001, "서버 내부에 오류가 발생했습니다."),

    USER_ALREADY_EXISTS(CONFLICT, 2000, "이미 등록된 사용자입니다.")
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
