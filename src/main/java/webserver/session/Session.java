package webserver.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {
    private final static Map<String, String> sessionDB = new HashMap<>();

    // TODO : 1시간 후 만료시키기
    public static String setSession(String userId) {
        String sessionId;
        do {
            sessionId = UUID.randomUUID().toString();
        } while (getUserId(sessionId) != null);
        sessionDB.put(sessionId, userId);
        return sessionId;
    }

    public static String getUserId(String sessionId) {
        return sessionDB.get(sessionId);
    }

    public static void expireSession(String sessionId) {
        sessionDB.remove(sessionId);
    }
}
