package webserver.response;

import webserver.type.HttpStatusCode;

import java.util.List;
import java.util.Map;

public class HttpResponse {
    private final String version;
    private final HttpStatusCode httpStatusCode;
    private final Map<String, List<String>> header;
    private final byte[] body;

    public HttpResponse(String version, HttpStatusCode httpStatusCode, Map<String, List<String>> header, byte[] body) {
        this.version = version;
        this.httpStatusCode = httpStatusCode;
        this.header = header;
        this.body = body;
    }

    // TODO: 수정
    public static HttpResponse of(String protocol, HttpStatusCode httpStatusCode, Map<String, List<String>> header, byte[] body) {
        return new HttpResponse(protocol, httpStatusCode, header, body);
    }

    public String getVersion() {
        return version;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getResponseLine() {
        return getVersion() + " " + getHttpStatusCode().getMessage() + "\r\n";
    }

    public Map<String, List<String>> getHeader() {
        return header;
    }

    public byte[] getBody() {
        return body;
    }
}
