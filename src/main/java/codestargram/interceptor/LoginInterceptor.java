package codestargram.interceptor;

import codestargram.domain.user.db.UserDatabase;
import codestargram.domain.user.model.User;
import codestargram.exception.user.UserNotFoundException;
import webserver.http.type.*;
import webserver.interceptor.Interceptor;
import webserver.session.Cookie;
import webserver.session.Session;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class LoginInterceptor implements Interceptor {
    @Override
    public HttpResponse interceptResponse(HttpRequest httpRequest, HttpResponse httpResponse) {
        // 상태코드가 200이 아니면 그대로 반환
        HttpStatusCode httpStatusCode = httpResponse.getHttpStatusCode();
        if (httpStatusCode != HttpStatusCode.OK) {
            return httpResponse;
        }

        // 확장자가 html이 아니면 반환
        if (!httpResponse.getHeaders().getContentType().equals(MIMEType.html)) {
            return httpResponse;
        }

        // 로그인한 유저가 아니면 그대로 반환
        Cookie cookie = new Cookie(httpRequest.getHeader().get(HttpHeader.COOKIE));

        String sid = cookie.get("SID");
        boolean sidExists = sid != null;

        String userId = Session.getUserId(sidExists ? sid : "");
        boolean login = userId != null;

        if (!(sidExists && login)) {
            return httpResponse;
        }

        // headers
        Map<String, List<String>> headers = new HashMap<>();
        Set<Map.Entry<String, List<String>>> entries = httpResponse.getHeaders().entrySet();
        for (Map.Entry<String, List<String>> entry : entries) {
            headers.put(entry.getKey(), entry.getValue());
        }

        // 바디 변경
        byte[] body = httpResponse.getBody();
        // TODO : user 정보에 profile 이미지 추가 userProfileUrl
        User user = UserDatabase.findUserById(userId).orElseThrow(UserNotFoundException::new);
        String html = changeHeader(body, "/img/user-default.png", user.getName());

        return HttpResponse.status(httpStatusCode)
                .headers(headers)
                .body(html.getBytes());
    }

    private static String changeHeader(byte[] body, String userProfileUrl, String nickname) {
        String html = new String(body, StandardCharsets.UTF_8);

        int headerStart = html.indexOf("<header");
        int headerEnd = html.indexOf("</header>");

        if (headerStart != -1 && headerEnd != -1) {
            String htmlHeader = html.substring(headerStart, headerEnd + "</header>".length());
            html = html.replace(htmlHeader,
                    """
                          <header class="header">
                            <a href="/main"><img src="../img/signiture.svg"  alt="코드스타그램 로고"/></a>
                            <ul class="header__menu">
                              <li class="header__menu__item">
                                <a class="btn btn_ghost btn_size_s" href="/user/list">사용자 목록</a>
                              </li>
                              <li class="header__menu__item">
                                <div class="post__account">
                                  <img class="post__account__img"  src=""" + "\"" + userProfileUrl + "\"" + """
                                   alt="게시글 작성자 프로필 이미지"/>
                                  <p class="post__account__nickname">"""+ nickname + """
                                  </p>
                                </div>
                              </li>
                              <li class="header__menu__item">
                                <a class="btn btn_contained btn_size_s" href="/article">글쓰기</a>
                              </li>
                              <li class="header__menu__item">
                                <form action="/logout" method="post">
                                  <button id="logout-btn" class="btn btn_ghost btn_size_s">
                                    로그아웃
                                  </button>
                                </form>
                              </li>
                            </ul>
                          </header>""");
        }
        return html;
    }
}
