package webserver.exception.common;

import webserver.exception.ApplicationException;
import webserver.type.HttpStatusCode;

public class InternalServerErrorException extends ApplicationException {
    public InternalServerErrorException() {
        super(HttpStatusCode.INTERNAL_SERVER_ERROR, HttpStatusCode.INTERNAL_SERVER_ERROR.getMessage());
    }

    public InternalServerErrorException(String errorMessage) {
        super(HttpStatusCode.INTERNAL_SERVER_ERROR, errorMessage);
    }

    public InternalServerErrorException(String errorMessage, Exception cause) {
        super(HttpStatusCode.INTERNAL_SERVER_ERROR, errorMessage, cause);
    }
}
