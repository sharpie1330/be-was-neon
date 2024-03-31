package webserver.filter;

import webserver.http.type.HttpRequest;

import java.util.List;

public class Filtering {

    private final List<Filter> filters;

    public Filtering(List<Filter> filters) {
        this.filters = filters;
    }

    public void filter(HttpRequest httpRequest) throws Exception {
        for (Filter filter : filters) {
            filter.doFilter(httpRequest);
        }
    }
}
