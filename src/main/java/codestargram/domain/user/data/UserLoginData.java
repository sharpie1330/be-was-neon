package codestargram.domain.user.data;

import webserver.annotation.CheckValid;

public class UserLoginData {
    @CheckValid(regex = "\\s*", included = false, message = "유저 아이디는 필수 값입니다.")
    private final String userId;
    @CheckValid(regex = "\\s*", included = false, message = "비밀번호는 필수 값입니다.")
    private final String password;

    public UserLoginData(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }
}
