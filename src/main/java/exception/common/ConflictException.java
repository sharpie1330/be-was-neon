package exception.common;

import exception.CustomException;
import webserver.type.HttpStatusCode;

public class ConflictException extends CustomException {
    public ConflictException() {
        super(HttpStatusCode.CONFLICT);
    }

    public ConflictException(String errorMessage) {
        super(HttpStatusCode.CONFLICT, errorMessage);
    }
}
