package webserver.common;

import webserver.type.MIMEType;
import webserver.utils.PropertyUtils;
import webserver.utils.RequestHeaderParser;

import java.util.*;

public class HttpHeader{
    private static final String charset = PropertyUtils.loadProperties().getProperty("charset");
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String LOCATION = "Location";

    private final Map<String, List<String>> headers;

    public HttpHeader() {
        headers = new HashMap<>();
    }

    public static HttpHeader of(List<String> headers) {
        return new HttpHeader(RequestHeaderParser.parse(headers));
    }

    public HttpHeader(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public long getContentLength() {
        String contentLength = getFirst(CONTENT_LENGTH);
        return contentLength != null ? Long.parseLong(contentLength) : -1L;
    }

    public void setContentLength(long contentLength) {
        set(CONTENT_LENGTH, String.valueOf(contentLength));
    }

    public MIMEType getContentType() {
        String contentType = getFirst(CONTENT_TYPE);
        return MIMEType.getMimeType(contentType);
    }

    public void setContentType(MIMEType mimeType) {
        setOrRemove(CONTENT_TYPE, mimeType.getMimeType());
    }

    public String getLocation() {
        return getFirst(LOCATION);
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

    public List<String> get(String headerName) {
        return headers.get(headerName);
    }

    public String getFirst(String headerName) {
        List<String> headerValue = get(headerName);
        return headerValue != null && !values().isEmpty() ? headerValue.get(0) : null;
    }

    public void set(String headerName, String headerValue) {
        List<String> values = new ArrayList<>(1);
        values.add(headerValue);
        this.headers.put(headerName, values);
    }

    public void setOrRemove(String headerName, String headerValue) {
        if (headerValue != null) {
            set(headerName, headerValue);
        } else {
            headers.remove(headerName);
        }
    }

    public Set<String> keySet() {
        return headers.keySet();
    }

    public Collection<List<String>> values() {
        return headers.values();
    }

    public Set<Map.Entry<String, List<String>>> entrySet() {
        return headers.entrySet();
    }
}
