package webserver.exception.server;

import webserver.exception.common.BadRequestException;

public class MalformedUrlFormatException extends BadRequestException {
    public MalformedUrlFormatException() {
        super("잘못된 요청 url 형식입니다.");
    }
}
