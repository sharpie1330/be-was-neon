package codestargram.filter;

import codestargram.domain.user.db.UserDatabase;
import codestargram.exception.user.UserNotFoundException;
import webserver.exception.server.UnAuthorizedException;
import webserver.filter.Filter;
import webserver.http.type.HttpHeader;
import webserver.http.type.HttpRequest;
import webserver.session.Cookie;
import webserver.session.Session;
import webserver.utils.URLUtils;

import java.util.Collections;
import java.util.List;

public class LoginFilter implements Filter {

    private static final List<String> AUTH_BLACK_LIST =
            List.of("/main", "/article");

    @Override
    public void doFilter(HttpRequest httpRequest) throws UnAuthorizedException {
        String path = URLUtils.getPurePath(httpRequest.getRequestLine().getURL());

        if (AUTH_BLACK_LIST.stream().noneMatch(path::startsWith)) {
            return;
        }

        Cookie cookie = new Cookie(httpRequest.getHeader().getOrDefault(HttpHeader.COOKIE, Collections.emptyList()));
        UnAuthorizedException unauthorizedException = new UnAuthorizedException(httpRequest.getRequestLine().getURL());

        String SID = cookie.get("SID");
        if (SID == null) {
            throw unauthorizedException;
        }

        String userId = Session.getUserId(SID);
        if (userId == null) {
            throw unauthorizedException;
        }

        UserDatabase.findUserById(userId).orElseThrow(UserNotFoundException::new);
    }
}
