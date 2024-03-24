package exception.common;

import exception.CustomException;
import webserver.type.HttpStatusCode;

public class NotFoundException extends CustomException {

    public NotFoundException() {
        super(HttpStatusCode.NOT_FOUND);
    }

    public NotFoundException(String errorMessage) {
        super(HttpStatusCode.NOT_FOUND, errorMessage);
    }
}
