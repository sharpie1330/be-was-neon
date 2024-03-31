package webserver.exception.server;

import webserver.exception.common.ServerException;
import webserver.http.type.HttpStatusCode;

public class UnauthorizedException extends ServerException
{
    public UnauthorizedException() {
        super(HttpStatusCode.UNAUTHORIZED, "인증이 필요한 요청입니다.");
    }

    public UnauthorizedException(String errorMessage) {
        super(HttpStatusCode.UNAUTHORIZED, errorMessage);
    }
}
