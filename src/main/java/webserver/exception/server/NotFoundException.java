package webserver.exception.server;

import webserver.exception.common.ApplicationException;
import webserver.type.HttpStatusCode;

public class NotFoundException extends ApplicationException {

    public NotFoundException() {
        super(HttpStatusCode.NOT_FOUND, HttpStatusCode.NOT_FOUND.getMessage());
    }

    public NotFoundException(String errorMessage) {
        super(HttpStatusCode.NOT_FOUND, errorMessage);
    }
}
