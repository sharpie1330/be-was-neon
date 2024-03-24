package exception.common;

import exception.CustomException;
import webserver.type.HttpStatusCode;

public class MethodNotAllowedException extends CustomException {

    public MethodNotAllowedException() {
        super(HttpStatusCode.METHOD_NOT_ALLOWED);
    }

    public MethodNotAllowedException(String errorMessage) {
        super(HttpStatusCode.METHOD_NOT_ALLOWED, errorMessage);
    }
}
