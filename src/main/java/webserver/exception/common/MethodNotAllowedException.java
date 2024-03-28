package webserver.exception.common;

import webserver.exception.HttpRequestException;
import webserver.type.HttpStatusCode;

public class MethodNotAllowedException extends HttpRequestException {

    public MethodNotAllowedException() {
        super(HttpStatusCode.METHOD_NOT_ALLOWED, HttpStatusCode.METHOD_NOT_ALLOWED.getMessage());
    }

    public MethodNotAllowedException(String errorMessage) {
        super(HttpStatusCode.METHOD_NOT_ALLOWED, errorMessage);
    }
}
