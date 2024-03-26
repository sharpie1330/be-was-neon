package webserver.common;

public class HttpBody {
    private final byte[] body;

    public HttpBody() {
        this.body = null;
    }

    public HttpBody(byte[] body) {
        this.body = body;
    }

    public byte[] getBody() {
        return body;
    }
}
