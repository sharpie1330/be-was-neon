package webserver.exception.request;

import webserver.exception.server.BadRequestException;

public class UnsupportedContentTypeException extends BadRequestException {
    public UnsupportedContentTypeException() {
        super("Unsupported Content Type");
    }
}
