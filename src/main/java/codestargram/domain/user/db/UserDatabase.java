package codestargram.domain.user.db;

import codestargram.domain.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserDatabase {
    private static final Map<String, User> users = new HashMap<>();

    public static void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    public static Optional<User> findUserById(String userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public static Collection<User> findAll() {
        return users.values();
    }
}
