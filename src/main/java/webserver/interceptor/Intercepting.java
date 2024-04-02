package webserver.interceptor;

import webserver.http.type.HttpRequest;
import webserver.http.type.HttpResponse;

import java.util.List;

public class Intercepting {
    private final List<Interceptor> interceptors;

    public Intercepting(List<Interceptor> interceptors) {
        this.interceptors = interceptors;
    }

    public HttpResponse interceptResponse(HttpRequest httpRequest, HttpResponse httpResponse) {
        HttpResponse intercepted = httpResponse;
        for (Interceptor interceptor : interceptors) {
            intercepted = interceptor.interceptResponse(httpRequest, intercepted);
        }
        return intercepted;
    }
}
