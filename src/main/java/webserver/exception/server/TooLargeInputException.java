package webserver.exception.server;

import webserver.exception.HttpRequestException;
import webserver.type.HttpStatusCode;

public class TooLargeInputException extends HttpRequestException {

    public TooLargeInputException() {
        super(HttpStatusCode.BAD_REQUEST, "The length of the HTTP request body exceeds the maximum size that the system can handle.");
    }

    public TooLargeInputException(String errorMessage) {
        super(HttpStatusCode.BAD_REQUEST, errorMessage);
    }
}
