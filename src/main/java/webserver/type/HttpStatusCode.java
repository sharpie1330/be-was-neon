package webserver.type;

public enum HttpStatusCode {
    OK("200 OK"),
    CREATED("201 Created"),
    MOVED_PERMANENTLY("301 Moved Permanently"),
    BAD_REQUEST("400 Bad Request"),
    NOT_FOUND("404 Not Found"),
    METHOD_NOT_ALLOWED("405 Method Not Allowed"),
    CONFLICT("409 Conflict"),
    INTERNAL_SERVER_ERROR("500 Internal Server Error")
    ;

    private final String message;

    HttpStatusCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
