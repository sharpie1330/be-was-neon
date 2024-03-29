package webserver.exception;

import webserver.response.HttpResponse;

public interface ExceptionHandler {
    HttpResponse handleException(Exception e);
}
