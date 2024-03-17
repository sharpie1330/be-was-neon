package webserver.response;

import webserver.type.HttpStatusCode;

import java.util.List;
import java.util.Map;

public class HttpResponse {
    private final String protocol;
    private final HttpStatusCode httpStatusCode;
    private final Map<String, List<String>> header;
    private final byte[] body;

    public HttpResponse(String protocol, HttpStatusCode httpStatusCode, Map<String, List<String>> header, byte[] body) {
        this.protocol = protocol;
        this.httpStatusCode = httpStatusCode;
        this.header = header;
        this.body = body;
    }

    // TODO: 수정
    public static HttpResponse of(String protocol, HttpStatusCode httpStatusCode, Map<String, List<String>> header, byte[] body) {
        return new HttpResponse(protocol, httpStatusCode, header, body);
    }

    public String getProtocol() {
        return protocol;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    public Map<String, List<String>> getHeader() {
        return header;
    }

    public byte[] getBody() {
        return body;
    }
}
