package webserver.response;

import webserver.type.HttpStatusCode;
import webserver.type.MIMEType;
import webserver.utils.PropertyUtils;

import java.util.*;

public class HttpResponse {
    private static final String charset = PropertyUtils.loadProperties().getProperty("charset");

    private final String version;
    private final HttpStatusCode httpStatusCode;
    private final Map<String, List<String>> headers;
    private final byte[] body;

    public HttpResponse(String version, HttpStatusCode httpStatusCode, Map<String, List<String>> headers, byte[] body) {
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

    public Map<String, List<String>> getHeaders() {
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

    public static Builder created(String version, String location) {
        return new ResponseBuilder(version, HttpStatusCode.CREATED).location(location);
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
        private final Map<String, List<String>> headers = new HashMap<>();

        public ResponseBuilder(String version, HttpStatusCode httpStatusCode) {
            this.version = version;
            this.httpStatusCode = httpStatusCode;
        }

        @Override
        public ResponseBuilder header(String headerName, String... headerValues) {
            List<String> headerValueList = headers.computeIfAbsent(headerName, k -> new ArrayList<>());
            headerValueList.addAll(Arrays.asList(headerValues));
            return this;
        }

        @Override
        public ResponseBuilder headers(Map<String, List<String>> headers) {
            if (headers != null) {
                this.headers.putAll(headers);
            }

            return this;
        }

        @Override
        public ResponseBuilder contentLength(long contentLength) {
            this.header("Content-Length", String.valueOf(contentLength));
            return this;
        }

        @Override
        public ResponseBuilder contentType(MIMEType mimeType) {
            this.header("Content-Type", mimeType.getMimeType(), "charset=" + charset);
            return this;
        }

        @Override
        public ResponseBuilder location(String location) {
            this.header("Location", location);
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
