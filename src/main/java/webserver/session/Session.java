package webserver.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Session {
    private static final Logger logger = LoggerFactory.getLogger(Session.class);

    private final static Map<String, String> sessionDB = new HashMap<>();
    private final static Map<String, Instant> sessionExpiration = new HashMap<>();
    private static final Duration SESSION_DURATION = Duration.ofHours(1);
    private static final ScheduledExecutorService cleanupExecutor = Executors.newSingleThreadScheduledExecutor();

    static {
        cleanupExecutor.scheduleAtFixedRate(Session::cleanupExpiredSessions, 1, 1, TimeUnit.HOURS);
    }

    public static String setSession(String userId) {
        String sessionId;
        Instant expirationTime = Instant.now().plus(SESSION_DURATION);
        do {
            sessionId = UUID.randomUUID().toString();
        } while (getUserId(sessionId) != null);
        sessionDB.put(sessionId, userId);
        sessionExpiration.put(sessionId, expirationTime);
        return sessionId;
    }

    public static String getUserId(String sessionId) {
        if (isSessionValid(sessionId)) {
            return sessionDB.get(sessionId);
        }
        return null;
    }

    public static void expireSession(String sessionId) {
        sessionDB.remove(sessionId);
    }

    private static boolean isSessionValid(String sessionId) {
        Instant expirationTime = sessionExpiration.get(sessionId);
        return expirationTime != null && Instant.now().isBefore(expirationTime);
    }

    private static void cleanupExpiredSessions() {
        Instant now = Instant.now();
        sessionExpiration.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
        sessionDB.entrySet().removeIf(entry -> !sessionExpiration.containsKey(entry.getKey()));
        logger.info("Expired Sessions are cleaned up!");
    }
}
