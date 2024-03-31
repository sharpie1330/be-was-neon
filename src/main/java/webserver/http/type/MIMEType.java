package webserver.http.type;

import java.util.Arrays;

public enum MIMEType {
    txt("txt/plain"),
    urlencoded("application/x-www-form-urlencoded"),
    svg("image/svg+xml"),
    ico("image/vnd.microsoft.icon"),
    css("text/css"),
    html("text/html"),
    png("image/png"),
    jsp("text/html"),
    jpg("image/jpeg"),
    json("application/json");

    private final String mimeType;

    MIMEType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeTypeByExtension() {
        return mimeType;
    }

    public static MIMEType getMimeTypeByExtension(String extension) {
        return Arrays.stream(MIMEType.values())
                .filter(m -> m.name().equals(extension))
                .findFirst()
                .orElse(txt);    // default : txt
    }

    public static MIMEType getMimeTypeByContentType(String contentType) {
        return Arrays.stream(MIMEType.values())
                .filter(m -> m.getMimeTypeByExtension().equals(contentType))
                .findFirst()
                .orElse(txt);
    }
}
