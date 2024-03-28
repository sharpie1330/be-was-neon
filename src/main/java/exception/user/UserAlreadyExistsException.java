package exception.user;

import exception.common.ConflictException;

public class UserAlreadyExistsException extends ConflictException {
    public UserAlreadyExistsException() {
        super("이미 존재하는 유저입니다.");
    }
}
