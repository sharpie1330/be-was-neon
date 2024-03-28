package webserver.exception;

import webserver.type.HttpStatusCode;

public class HttpRequestException extends Exception{
    private final HttpStatusCode httpStatusCode;
    private final String errorMessage;
    private final Exception cause;

    public HttpRequestException(HttpStatusCode httpStatusCode, String errorMessage) {
        this.httpStatusCode = httpStatusCode;
        this.errorMessage = errorMessage;
        this.cause = null;
    }

    public HttpRequestException(HttpStatusCode httpStatusCode, String errorMessage, Exception cause) {
        this.httpStatusCode = httpStatusCode;
        this.errorMessage = errorMessage;
        this.cause = cause;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }
}
