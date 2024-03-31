package codestargram.exception.user;

import webserver.exception.server.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException() {
        super("사용자를 찾을 수 없습니다.");
    }
}
