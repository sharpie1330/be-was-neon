package exception.common;

import exception.CustomException;
import webserver.type.HttpStatusCode;

public class BadRequestException extends CustomException {
    public BadRequestException(){
        super(HttpStatusCode.BAD_REQUEST);
    }

    public BadRequestException(String errorMessage) {
        super(HttpStatusCode.BAD_REQUEST, errorMessage);
    }
}
