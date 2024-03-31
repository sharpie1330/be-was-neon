package webserver.http.type;

public class HttpRequestLine {
    private final HttpMethod httpMethod;
    private final String URL;
    private final String version;

    public HttpRequestLine(HttpMethod httpMethod, String URL, String version) {
        this.httpMethod = httpMethod;
        this.URL = URL;
        this.version = version;
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
}
