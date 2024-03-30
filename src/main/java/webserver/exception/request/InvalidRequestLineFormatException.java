package webserver.exception.request;

import webserver.exception.server.BadRequestException;

public class InvalidRequestLineFormatException extends BadRequestException {
    public InvalidRequestLineFormatException() {
        super("Invalid request line format. Please check the request line and ensure it follows the correct HTTP request format.");
    }
}
