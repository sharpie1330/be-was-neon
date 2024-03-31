package webserver.session;

import webserver.utils.Delimiter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

public class Cookie {

    private final Map<String, String> cookie;

    public Cookie() {
        cookie = new LinkedHashMap<>();
    }

    public String get(String key) {
        return cookie.get(key);
    }

    public void setCookie(String key, String value) {
        cookie.put(key, value);
    }

    public void setPath(String path) {
        cookie.put("Path", path);
    }

    public void setSID(String sessionID) {
        cookie.put("SID", sessionID);
    }

    public void setMaxAge(int maxAge) {
        cookie.put("Max-Age", String.valueOf(maxAge));
    }

    @Override
    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(Delimiter.SEMICOLON);
        for (Map.Entry<String, String> entry : cookie.entrySet()) {
            stringJoiner.add(entry.getKey() + "=" + entry.getValue());
        }

        return stringJoiner.toString();
    }
}
