package webserver.filter;

import webserver.http.type.HttpRequest;

public interface Filter {
    void doFilter(HttpRequest httpRequest) throws Exception;
}
