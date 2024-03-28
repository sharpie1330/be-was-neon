package exception.common;

import exception.ApplicationException;
import webserver.type.HttpStatusCode;

public class BadRequestException extends ApplicationException {
    public BadRequestException(){
        super(HttpStatusCode.BAD_REQUEST, HttpStatusCode.BAD_REQUEST.getMessage());
    }

    public BadRequestException(String errorMessage) {
        super(HttpStatusCode.BAD_REQUEST, errorMessage);
    }
}
