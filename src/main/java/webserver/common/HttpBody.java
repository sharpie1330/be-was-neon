package webserver.common;

import webserver.utils.RequestParser;

import java.util.*;

public class HttpBody {
    private final Map<String, String> body;

    public HttpBody() {
        this.body = new HashMap<>();
    }

    public static HttpBody of(String body) {
        return body.isEmpty() ? new HttpBody() : new HttpBody(RequestParser.parseBody(body));
    }

    public HttpBody(Map<String, String> body) {
        this.body = body;
    }

    public void set(String key, String value) {
        body.put(key, value);
    }

    public void setAll(Map<String, String> body) {
        this.body.putAll(body);
    }

    public String get(String key) {
        return body.get(key);
    }

    public Collection<String> values() {
        return body.values();
    }

    public Set<Map.Entry<String, String>> entrySet() {
        return body.entrySet();
    }

    public Set<String> keySet() {
        return body.keySet();
    }
}
