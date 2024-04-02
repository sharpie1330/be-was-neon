package webserver.http.type;

import java.util.*;

public class HttpResponse {
    private final HttpStatusCode httpStatusCode;
    private final HttpHeader headers;
    private final byte[] body;

    public HttpResponse(HttpStatusCode httpStatusCode, HttpHeader headers, byte[] body) {
        this.httpStatusCode = httpStatusCode;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse of(byte[] body) {
        if (body != null) {
            return HttpResponse.ok(body);
        }
        return notFound().build();
    }

    public String getVersion() {
        return "HTTP/1.1";
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getResponseLine() {
        return getVersion() + " " + getHttpStatusCode().getMessage() + "\r\n";
    }

    public HttpHeader getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public static Builder status(HttpStatusCode httpStatusCode) {
        return new ResponseBuilder(httpStatusCode);
    }

    public static Builder ok() {
        return new ResponseBuilder(HttpStatusCode.OK);
    }

    public static HttpResponse ok(byte[] body) {
        return ok().body(body);
    }

    public static Builder created() {
        return new ResponseBuilder(HttpStatusCode.CREATED);
    }

    public static Builder found(String location) {
        return new ResponseBuilder(HttpStatusCode.FOUND).location(location);
    }

    public static Builder badRequest() {
        return new ResponseBuilder(HttpStatusCode.BAD_REQUEST);
    }

    public static Builder notFound() {
        return new ResponseBuilder(HttpStatusCode.NOT_FOUND);
    }

    public static Builder internalServerError() {
        return new ResponseBuilder(HttpStatusCode.INTERNAL_SERVER_ERROR);
    }

    private static class ResponseBuilder implements Builder{
        private final HttpStatusCode httpStatusCode;
        private final HttpHeader headers;

        public ResponseBuilder(HttpStatusCode httpStatusCode) {
            this.httpStatusCode = httpStatusCode;
            this.headers = new HttpHeader();
        }

        @Override
        public ResponseBuilder header(String headerName, String... headerValues) {
            headers.addAll(headerName, Arrays.stream(headerValues).toList());
            return this;
        }

        @Override
        public ResponseBuilder headers(Map<String, List<String>> headers) {
            this.headers.addAll(headers);

            return this;
        }

        @Override
        public ResponseBuilder contentLength(int contentLength) {
            headers.setContentLength(contentLength);
            return this;
        }

        @Override
        public ResponseBuilder contentType(MIMEType mimeType) {
            headers.setContentType(mimeType);
            return this;
        }

        @Override
        public ResponseBuilder location(String location) {
            headers.setLocation(location);
            return this;
        }

        @Override
        public HttpResponse build() {
            return this.body(null);
        }

        @Override
        public HttpResponse body(byte[] body) {
            return new HttpResponse(httpStatusCode, headers, body);
        }
    }

    public interface Builder{
        Builder header(String headerName, String... headerValues);

        Builder headers(Map<String, List<String>> headers);

        Builder contentLength(int contentLength);

        Builder contentType(MIMEType mimeType);

        Builder location(String location);

        HttpResponse body(byte[] body);

        HttpResponse build();
    }
}
