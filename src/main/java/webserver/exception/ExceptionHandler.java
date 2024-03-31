package webserver.exception;

import webserver.http.type.HttpResponse;

public interface ExceptionHandler {
    HttpResponse handleException(Exception e);
}
