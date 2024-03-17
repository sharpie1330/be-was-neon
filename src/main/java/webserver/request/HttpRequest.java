package webserver.request;

import webserver.type.HttpMethod;
import webserver.utils.RequestHeaderParser;

import java.util.List;
import java.util.Map;

public class HttpRequest {
    private final HttpMethod httpMethod;
    private final String URL;
    private final String version;
    private final Map<String, List<String>> header;
    private final String body;

    public HttpRequest(HttpMethod httpMethod, String URL, String version, Map<String, List<String>> header, String body) {
        this.httpMethod = httpMethod;
        this.URL = URL;
        this.version = version;
        this.header = header;
        this.body = body;
    }

    public static HttpRequest of(String startLine, List<String> requestHeader, String requestBody) {
        // startLine parse
        String[] tokens = startLine.split(" ");
        String requestMethod = tokens[0];
        String requestUrl = tokens[1];
        String version = tokens[2];

        // header parse
        RequestHeaderParser requestHeaderParser = new RequestHeaderParser();
        Map<String, List<String>> parsedHeader = requestHeaderParser.parse(requestHeader);

        return new HttpRequest(HttpMethod.get(requestMethod), requestUrl, version, parsedHeader, requestBody);
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

    public Map<String, List<String>> getHeader() {
        return header;
    }

    public String getBody() {
        return body;
    }
}
