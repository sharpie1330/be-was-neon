package codestargram.filter;

import codestargram.domain.user.db.UserDatabase;
import codestargram.exception.user.UserNotFoundException;
import webserver.exception.server.UnauthorizedException;
import webserver.filter.Filter;
import webserver.http.type.HttpRequest;
import webserver.session.Cookie;
import webserver.session.Session;
import webserver.utils.Delimiter;
import webserver.utils.URLUtils;

import java.util.List;

public class LoginFilter implements Filter {

    // Fixme: 모든 걸 다 쓸 순 없음... 수정할 것
    private static final List<String> AUTH_BLACK_LIST =
            List.of("/main", "/main/index.html", "/article", "/article/index.html");

    @Override
    public void doFilter(HttpRequest httpRequest) throws UnauthorizedException {
        // white list의 path와 일치하지 않으면 건너뛰기
        String path = URLUtils.getPath(httpRequest.getRequestLine().getURL());
        if (AUTH_BLACK_LIST.stream().noneMatch(path::equals)) {
            return;
        }

        List<String> cookies = httpRequest.getHeader().get("Cookie");
        if (cookies == null) {
            throw new UnauthorizedException();
        }

        Cookie clientCookie = new Cookie();
        for (String cookie : cookies) {
            String[] keyValue = cookie.trim().split(Delimiter.EQUAL, 2);
            clientCookie.setCookie(keyValue[0], keyValue[1]);
        }

        String SID = clientCookie.get("SID");
        if (SID == null) {
            throw new UnauthorizedException();
        }

        String userId = Session.getUserId(SID);
        UserDatabase.findUserById(userId).orElseThrow(UserNotFoundException::new);
    }
}
