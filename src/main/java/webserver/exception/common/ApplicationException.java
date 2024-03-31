package webserver.exception.common;

import webserver.http.type.HttpStatusCode;

public class ApplicationException extends RuntimeException{
    private final HttpStatusCode httpStatusCode;
    private final String errorMessage;
    private final Exception cause;

    public ApplicationException(HttpStatusCode httpStatusCode, String errorMessage) {
        this.httpStatusCode = httpStatusCode;
        this.errorMessage = errorMessage;
        this.cause = null;
    }

    public ApplicationException(HttpStatusCode httpStatusCode, String errorMessage, Exception cause) {
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
