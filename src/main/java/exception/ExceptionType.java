package exception;

import exception.common.BadRequestException;
import exception.common.MethodNotAllowedException;
import exception.server.MalformedBodyFormatException;
import exception.server.MalformedUrlFormatException;
import exception.server.PathNotFoundException;
import exception.user.UserAlreadyExistsException;

import java.util.Arrays;

public enum ExceptionType {
    // 1XXX 서버 요청, 응답 오류
    UNHANDLED_SERVER_ERROR(1000, "서버 내부에 오류가 발생했습니다.", null),
    PATH_NOT_FOUND(1001, "존재하지 않는 경로입니다.", PathNotFoundException.class),
    MALFORMED_URL_FORMAT(1002, "잘못된 URL 형식입니다.", MalformedUrlFormatException.class),
    MALFORMED_BODY_FORMAT(1003, "잘못된 Request Body 형식입니다.", MalformedBodyFormatException.class),
    INVALID_REQUEST_METHOD(1004, "지원하지 않는 요청 메서드입니다.", MethodNotAllowedException.class),
    INVALID_VALUE(1005, "요청 값이 유효하지 않습니다.", BadRequestException.class),

    // 2XXX 사용자 관련 오류
    USER_ALREADY_EXISTS(2000, "이미 등록된 사용자입니다.", UserAlreadyExistsException.class),
    ;

    private final int errorCode;
    private final String errorMessage;
    private final Class<? extends Exception> exceptionClass;

    ExceptionType(int errorCode, String errorMessage, Class<? extends Exception> exceptionClass) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.exceptionClass = exceptionClass;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Class<? extends Exception> getExceptionClass() {
        return exceptionClass;
    }

    public static ExceptionType from(Class<? extends Exception> exceptionClass) {
        return Arrays.stream(values())
                .filter(exception -> exception.getExceptionClass() != null && exception.getExceptionClass().equals(exceptionClass))
                .findFirst()
                .orElse(UNHANDLED_SERVER_ERROR);
    }
}
