package webserver.route.user.data;

import model.User;

public class UserSaveData {
    private final String userId;
    private final String password;
    private final String nickname;
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
