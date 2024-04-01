package codestargram.filter;

import codestargram.domain.user.db.UserDatabase;
import webserver.exception.server.UnauthorizedException;
import webserver.filter.Filter;
import webserver.http.type.HttpRequest;
import webserver.session.Session;
import webserver.utils.Delimiter;
import webserver.utils.URLUtils;

import java.util.Collections;
import java.util.List;

public class LoginFilter implements Filter {

    // Fixme: 모든 걸 다 쓸 순 없음... 수정할 것
    private static final List<String> AUTH_BLACK_LIST =
            List.of("/main", "/main/index.html", "/article", "/article/index.html");

    @Override
    public void doFilter(HttpRequest httpRequest) throws UnauthorizedException {
        String path = URLUtils.getPath(httpRequest.getRequestLine().getURL());
        if (AUTH_BLACK_LIST.contains(path)) {
            List<String> cookies = httpRequest.getHeader().getOrDefault("Cookie", Collections.emptyList());

            String SID = cookies.stream()
                    .map(cookie -> cookie.trim().split(Delimiter.EQUAL, 2))
                    .filter(kv -> kv.length == 2 && kv[0].equals("SID"))
                    .map(kv -> kv[1])
                    .findFirst()
                    .orElseThrow(UnauthorizedException::new);

            String userId = Session.getUserId(SID);
            if (userId == null || UserDatabase.findUserById(userId).isEmpty()) {
                throw new UnauthorizedException();
            }
        }
    }
}
