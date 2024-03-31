package webserver.http.type;

public class HttpRequest {
    private final HttpRequestLine requestLine;
    private final HttpHeader header;
    private final HttpBody body;

    public HttpRequest(HttpRequestLine requestLine, HttpHeader header, HttpBody body) {
        this.requestLine = requestLine;
        this.header = header;
        this.body = body;
    }

    public HttpRequestLine getRequestLine() {
        return requestLine;
    }

    public HttpHeader getHeader() {
        return header;
    }

    public HttpBody getBody() {
        return body;
    }
}
