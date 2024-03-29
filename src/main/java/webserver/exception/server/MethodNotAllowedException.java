package webserver.exception.server;

import webserver.exception.common.ServerException;
import webserver.type.HttpStatusCode;

public class MethodNotAllowedException extends ServerException {

    public MethodNotAllowedException() {
        super(HttpStatusCode.METHOD_NOT_ALLOWED, HttpStatusCode.METHOD_NOT_ALLOWED.getMessage());
    }

    public MethodNotAllowedException(String errorMessage) {
        super(HttpStatusCode.METHOD_NOT_ALLOWED, errorMessage);
    }
}
