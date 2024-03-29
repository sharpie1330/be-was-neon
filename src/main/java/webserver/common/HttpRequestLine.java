package webserver.common;

import webserver.type.HttpMethod;
import webserver.utils.Delimiter;

public class HttpRequestLine {
    private HttpMethod httpMethod;
    private String URL;
    private String version;

    public HttpRequestLine(String requestLine) {
        String[] split = requestLine.split(Delimiter.SPACE);

        // TODO: MethodNotAllowedException
        if (split.length == 3) {
            this.httpMethod = HttpMethod.get(split[0]);
            this.URL = split[1];
            this.version = split[2];
        }
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

    public boolean isNotValid() {
        return httpMethod == null || version == null || !version.toUpperCase().contains("HTTP");
    }
}
