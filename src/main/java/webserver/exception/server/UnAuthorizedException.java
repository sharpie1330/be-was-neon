package webserver.exception.server;

import webserver.exception.common.ServerException;
import webserver.http.type.HttpStatusCode;

public class UnAuthorizedException extends ServerException
{
    public UnAuthorizedException() {
        super(HttpStatusCode.UNAUTHORIZED, "인증이 필요한 요청입니다.");
    }

    public UnAuthorizedException(String errorMessage) {
        super(HttpStatusCode.UNAUTHORIZED, errorMessage);
    }
}
