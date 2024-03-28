package webserver.exception.server;

import webserver.exception.common.BadRequestException;

public class MalformedBodyFormatException extends BadRequestException {
    public MalformedBodyFormatException() {
        super("요청 바디 형식이 잘못되었습니다.");
    }
}
