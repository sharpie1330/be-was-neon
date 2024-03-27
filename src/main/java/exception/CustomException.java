package exception;

import webserver.type.HttpStatusCode;

public class CustomException extends RuntimeException{
    private final HttpStatusCode httpStatusCode;
    private final int errorCode;
    private final String errorMessage;
    private final Exception cause;

    public CustomException(HttpStatusCode httpStatusCode) {
        ExceptionType exceptionType = ExceptionType.from(this.getClass());
        this.httpStatusCode = httpStatusCode;
        this.errorCode = exceptionType.getErrorCode();
        this.errorMessage = exceptionType.getErrorMessage();
        this.cause = null;
    }

    public CustomException(HttpStatusCode httpStatusCode, String errorMessage) {
        ExceptionType exceptionType = ExceptionType.from(this.getClass());
        this.httpStatusCode = httpStatusCode;
        this.errorCode = exceptionType.getErrorCode();
        this.errorMessage = errorMessage;
        this.cause = null;
    }

    public CustomException(HttpStatusCode httpStatusCode, Exception cause) {
        ExceptionType exceptionType = ExceptionType.from(this.getClass());
        this.httpStatusCode = httpStatusCode;
        this.errorCode = exceptionType.getErrorCode();
        this.errorMessage = exceptionType.getErrorMessage();
        this.cause = cause;
    }

    public CustomException(HttpStatusCode httpStatusCode, String errorMessage, Exception cause) {
        ExceptionType exceptionType = ExceptionType.from(this.getClass());
        this.httpStatusCode = httpStatusCode;
        this.errorCode = exceptionType.getErrorCode();
        this.errorMessage = errorMessage;
        this.cause = cause;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }
}
