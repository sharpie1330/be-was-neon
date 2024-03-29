package webserver.exception.request;

import webserver.exception.server.BadRequestException;

public class MalformedBodyFormatException extends BadRequestException {
    public MalformedBodyFormatException() {
        super("요청 바디 형식이 잘못되었습니다.");
    }
}
