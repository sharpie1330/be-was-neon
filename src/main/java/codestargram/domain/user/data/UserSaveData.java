package codestargram.domain.user.data;

import codestargram.domain.user.model.User;
import webserver.annotation.CheckValid;

public class UserSaveData {

    @CheckValid(regex = "\\s*", included = false, message = "유저 아이디는 필수 값입니다.")
    private final String userId;
    @CheckValid(regex = "\\s*", included = false, message = "비밀번호는 필수 값입니다.")
    private final String password;
    @CheckValid(regex = "\\s*", included = false, message = "닉네임은 필수 값입니다.")
    private final String nickname;
    @CheckValid(regex = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
            message = "올바른 이메일 형식이어야 합니다.")
    private final String email;

    public UserSaveData(String userId, String password, String nickname, String email) {
        this.userId = userId;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public User makeUser() {
        return new User(userId, password, nickname, email);
    }
}
