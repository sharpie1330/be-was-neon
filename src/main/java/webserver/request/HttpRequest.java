package webserver.request;

import webserver.common.HttpHeader;
import webserver.type.HttpMethod;

public class HttpRequest {
    private final HttpMethod httpMethod;
    private final String URL;
    private final String version;
    private final HttpHeader header;
    private final String body;

    public HttpRequest(HttpMethod httpMethod, String URL, String version, HttpHeader header, String body) {
        this.httpMethod = httpMethod;
        this.URL = URL;
        this.version = version;
        this.header = header;
        this.body = body;
    }

    public static HttpRequest of(String startLine, HttpHeader header, String requestBody) {
        // startLine parse
        String[] tokens = startLine.split(" ");
        String requestMethod = tokens[0];
        String requestUrl = tokens[1];
        String version = tokens[2];

        return new HttpRequest(HttpMethod.get(requestMethod), requestUrl, version, header, requestBody);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getURL() {
        return URL;
    }

    public String getVersion() {
        return version;
    }

    public HttpHeader getHeader() {
        return header;
    }

    public String getBody() {
        return body;
    }
}
