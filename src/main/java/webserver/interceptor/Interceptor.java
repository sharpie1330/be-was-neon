package webserver.interceptor;

import webserver.http.type.HttpRequest;
import webserver.http.type.HttpResponse;

public interface Interceptor {
    HttpResponse interceptResponse(HttpRequest httpRequest, HttpResponse httpResponse);
}
