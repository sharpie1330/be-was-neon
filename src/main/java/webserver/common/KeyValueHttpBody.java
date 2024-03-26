package webserver.common;

import webserver.utils.RequestParser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class KeyValueHttpBody {
    private final Map<String, String> body;

    private KeyValueHttpBody() {
        this.body = new HashMap<>();
    }

    public static KeyValueHttpBody of(HttpBody httpBody) {
        return httpBody.getBody() == null ? new KeyValueHttpBody() : new KeyValueHttpBody(RequestParser.parseBody(httpBody.getBody()));
    }

    public KeyValueHttpBody(Map<String, String> body) {
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
