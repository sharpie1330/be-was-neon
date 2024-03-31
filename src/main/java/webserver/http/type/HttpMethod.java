package webserver.http.type;

import java.util.HashMap;
import java.util.Map;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    PATCH,
    DELETE;

    private static final Map<String, HttpMethod> mappings = new HashMap<>(HttpMethod.values().length);

    HttpMethod() {

    }

    public static HttpMethod get(String method) {
        return mappings.get(method);
    }

    static {
        HttpMethod[] methods = values();

        for (HttpMethod httpMethod : methods) {
            mappings.put(httpMethod.name(), httpMethod);
        }
    }
}
