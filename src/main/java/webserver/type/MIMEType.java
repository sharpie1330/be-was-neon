package webserver.type;

import java.util.Arrays;

public enum MIMEType {
    txt("txt/plain"),
    svg("image/svg+xml"),
    ico("image/vnd.microsoft.icon"),
    css("text/css"),
    html("text/html"),
    png("image/png"),
    jsp("text/html"),
    jpg("image/jpeg");

    private final String mimeType;

    MIMEType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public static MIMEType getMimeType(String extension) {
        return Arrays.stream(MIMEType.values())
                .filter(m -> m.name().equals(extension))
                .findAny()
                .orElse(txt);    // default : txt
    }
}
