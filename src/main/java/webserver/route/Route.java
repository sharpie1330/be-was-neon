package webserver.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;
import webserver.route.staticPage.DynamicPageRequestManager;
import webserver.route.staticPage.StaticPageRequestManager;
import webserver.utils.PropertyUtils;
import webserver.utils.URLUtils;

import java.util.Map;

import static webserver.utils.PropertyUtils.loadStaticSourcePathFromProperties;

public class Route {
    private static final Logger logger = LoggerFactory.getLogger(Route.class);

    private static final String STATIC_SOURCE_PATH = loadStaticSourcePathFromProperties();
    private static final String DEFAULT_HTML = PropertyUtils.loadProperties().getProperty("defaultHtml");
    private static final String ERROR_404_HTML = PropertyUtils.loadProperties().getProperty("error404Html");

    private final Map<String, String> STATIC_MAPPING = Map.of(
            "/", DEFAULT_HTML,
            "/main", DEFAULT_HTML,
            "/article", DEFAULT_HTML,
            "/login", DEFAULT_HTML,
            "/registration", DEFAULT_HTML,
            "default", ERROR_404_HTML
    );

    private static final Route instance = new Route();

    private final StaticPageRequestManager staticPageRequestManager;
    private final DynamicPageRequestManager dynamicPageRequestManager;

    private Route() {
        dynamicPageRequestManager = DynamicPageRequestManager.getInstance();
        staticPageRequestManager = StaticPageRequestManager.getInstance();
    }

    public static Route getInstance() {
        return instance;
    }

    public HttpResponse route(HttpRequest httpRequest) throws Exception{
        String filePath = getFilePath(URLUtils.getPath(httpRequest.getRequestLine().getURL()));

        if (!filePath.equals("default")) {
            return staticPageRequestManager.routeStaticPage(httpRequest, filePath);
        }

        return dynamicPageRequestManager.routeDynamicPage(httpRequest);
    }

    private String getFilePath(String path) {
        // 파일 요청이면 기본 경로와 합쳐서 그대로 리턴
        if (!URLUtils.getExtension(path).isEmpty()) {
            return STATIC_SOURCE_PATH.concat(path);
        }

        // 해당 url 매핑이 있는지 확인
        String requestPath = STATIC_MAPPING.keySet().stream()
                .filter(key -> key.equals(path))
                .findAny()
                .orElse("default");

        // 매칭되는 url이 없으면 그대로 반환
        if (requestPath.equals("default")) {
            return requestPath;
        }

        return STATIC_SOURCE_PATH.concat(path)
                .concat("/")    // 구분자
                .concat(STATIC_MAPPING.get(requestPath));
    }

}
