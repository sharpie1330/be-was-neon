package webserver.response;

import webserver.common.HttpHeader;
import webserver.type.HttpStatusCode;
import webserver.type.MIMEType;

import java.util.*;

public class HttpResponse {
    private final String version;
    private final HttpStatusCode httpStatusCode;
    private final HttpHeader headers;
    private final byte[] body;

    public HttpResponse(String version, HttpStatusCode httpStatusCode, HttpHeader headers, byte[] body) {
        this.version = version;
        this.httpStatusCode = httpStatusCode;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse of(String version, byte[] body) {
        if (body != null) {
            return HttpResponse.ok(version, body);
        }
        return notFound(version).build();
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

    public HttpHeader getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public static Builder status(String version, HttpStatusCode httpStatusCode) {
        return new ResponseBuilder(version, httpStatusCode);
    }

    public static Builder ok(String version) {
        return new ResponseBuilder(version, HttpStatusCode.OK);
    }

    public static HttpResponse ok(String version, byte[] body) {
        return ok(version).body(body);
    }

    public static Builder created(String version) {
        return new ResponseBuilder(version, HttpStatusCode.CREATED);
    }

    public static Builder found(String version, String location) {
        return new ResponseBuilder(version, HttpStatusCode.FOUND).location(location);
    }

    public static Builder badRequest(String version) {
        return new ResponseBuilder(version, HttpStatusCode.BAD_REQUEST);
    }

    public static Builder notFound(String version) {
        return new ResponseBuilder(version, HttpStatusCode.NOT_FOUND);
    }

    public static Builder internalServerError(String version) {
        return new ResponseBuilder(version, HttpStatusCode.INTERNAL_SERVER_ERROR);
    }

    private static class ResponseBuilder implements Builder{
        private final String version;
        private final HttpStatusCode httpStatusCode;
        private final HttpHeader headers;

        public ResponseBuilder(String version, HttpStatusCode httpStatusCode) {
            this.version = version;
            this.httpStatusCode = httpStatusCode;
            this.headers = new HttpHeader();
        }

        public ResponseBuilder(String version, HttpStatusCode httpStatusCode, HttpHeader headers) {
            this.version = version;
            this.httpStatusCode = httpStatusCode;
            this.headers = headers;
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
        public ResponseBuilder contentLength(long contentLength) {
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
            return new HttpResponse(version, httpStatusCode, headers, body);
        }
    }

    public interface Builder{
        Builder header(String headerName, String... headerValues);

        Builder headers(Map<String, List<String>> headers);

        Builder contentLength(long contentLength);

        Builder contentType(MIMEType mimeType);

        Builder location(String location);

        HttpResponse body(byte[] body);

        HttpResponse build();
    }
}
