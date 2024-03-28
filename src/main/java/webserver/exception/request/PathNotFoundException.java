package webserver.exception.request;

import webserver.exception.common.NotFoundException;

public class PathNotFoundException extends NotFoundException {
    public PathNotFoundException() {
        super("해당 경로를 찾을 수 없습니다.");
    }
}
