package utils;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum MIMEType {
    svg("image/svg+xml"),
    ico("image/vnd.microsoft.icon"),
    css("text/css"),
    html("text/html"),
    png("image/png");

    private final String mimeType;

    MIMEType(String mimeType) {
        this.mimeType = mimeType;
    }

    public static String getContentType(String extension) {
        return Arrays.stream(MIMEType.values())
                .filter(m -> m.name().equals(extension))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 파일 확장자입니다."))
                .mimeType;
    }
}
