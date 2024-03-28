package webserver.exception.common;

import webserver.exception.ApplicationException;
import webserver.type.HttpStatusCode;

public class ConflictException extends ApplicationException {
    public ConflictException() {
        super(HttpStatusCode.CONFLICT, HttpStatusCode.CONFLICT.getMessage());
    }

    public ConflictException(String errorMessage) {
        super(HttpStatusCode.CONFLICT, errorMessage);
    }
}
