package webserver.common;

import webserver.type.MIMEType;
import webserver.utils.PropertyUtils;
import webserver.utils.RequestHeaderParser;

import java.util.*;

public class HttpHeader implements Map<String, List<String>> {
    private static final String charset = PropertyUtils.loadProperties().getProperty("charset");
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String LOCATION = "Location";

    private final Map<String, List<String>> headers;

    public HttpHeader() {
        headers = new HashMap<>();
    }

    public HttpHeader(List<String> headers) {
        this.headers = RequestHeaderParser.parse(headers);
    }

    public HttpHeader(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public void setContentLength(long contentLength) {
        set(CONTENT_LENGTH, String.valueOf(contentLength));
    }

    public void setContentType(MIMEType mimeType) {
        setOrRemove(CONTENT_TYPE, mimeType.getMimeType());
        add(CONTENT_TYPE, "charset=" + charset);
    }

    public void setLocation(String location) {
        setOrRemove(LOCATION, location);
    }

    public void add(String headerName, String headerValue) {
        List<String> values = this.headers.computeIfAbsent(headerName, (k) -> new ArrayList<>(1));
        values.add(headerValue);
    }

    public void addAll(String headerName, List<String> headerValues) {
        List<String> currentValues = this.headers.computeIfAbsent(headerName, (k) -> new ArrayList<>(headerValues.size()));
        currentValues.addAll(headerValues);
    }

    public void addAll(Map<String, List<String>> values) {
        values.forEach(this::addAll);
    }

    public void set(String headerName, String headerValue) {
        List<String> values = new ArrayList<>(1);
        values.add(headerValue);
        this.headers.put(headerName, values);
    }

    public void setOrRemove(String headerName, String headerValue) {
        if (headerValue != null) {
            this.set(headerName, headerValue);
        } else {
            this.remove(headerName);
        }
    }

    @Override
    public int size() {
        return headers.size();
    }

    @Override
    public boolean isEmpty() {
        return headers.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return headers.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return headers.containsValue(value);
    }

    @Override
    public List<String> get(Object key) {
        return headers.get(key);
    }

    @Override
    public List<String> put(String key, List<String> value) {
        return headers.put(key, value);
    }

    @Override
    public List<String> remove(Object key) {
        return headers.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends List<String>> m) {
        headers.putAll(m);
    }

    @Override
    public void clear() {
        headers.clear();
    }

    @Override
    public Set<String> keySet() {
        return headers.keySet();
    }

    @Override
    public Collection<List<String>> values() {
        return headers.values();
    }

    @Override
    public Set<Entry<String, List<String>>> entrySet() {
        return headers.entrySet();
    }
}
