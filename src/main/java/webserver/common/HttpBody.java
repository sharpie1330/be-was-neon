package webserver.common;

public class HttpBody {
    private final String body;

    public HttpBody() {
        this.body = "";
    }

    public HttpBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
