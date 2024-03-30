package webserver.exception.request;

import webserver.exception.server.BadRequestException;

public class InvalidHttpVersionException extends BadRequestException {
    public InvalidHttpVersionException() {
        super("Invalid HTTP version. Please use a valid HTTP version string such as 'HTTP/1.1'");
    }
}
