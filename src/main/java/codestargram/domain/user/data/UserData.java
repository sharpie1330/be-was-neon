package codestargram.domain.user.data;

public class UserData {
    private final String userId;
    private final String nickname;
    private final String email;

    public UserData(String userId, String nickname, String email) {
        this.userId = userId;
        this.nickname = nickname;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }
}
