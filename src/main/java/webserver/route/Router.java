package webserver.route;

import webserver.request.HttpRequest;
import webserver.response.HttpResponse;
import webserver.route.router.DynamicPageRouter;
import webserver.route.router.StaticPageRouter;
import webserver.type.HttpMethod;
import webserver.utils.PropertyUtils;
import webserver.utils.URLUtils;

import java.util.List;
import java.util.Map;

import static webserver.utils.PropertyUtils.loadStaticSourcePathFromProperties;

public class Router {
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

    private final StaticPageRouter staticPageRouter;
    private final DynamicPageRouter dynamicPageRouter;

    public Router(List<Class<?>> controllers) {
        this.dynamicPageRouter = new DynamicPageRouter(controllers);
        this.staticPageRouter = new StaticPageRouter();
    }

    public HttpResponse route(HttpRequest httpRequest) throws Exception{
        String filePath = getFilePath(URLUtils.getPath(httpRequest.getRequestLine().getURL()));

        // GET 요청이고, STATIC_MAPPING에서 경로 조회한 결과가 default인 경우에만 정적 페이지 라우팅
        if (httpRequest.getRequestLine().getHttpMethod().equals(HttpMethod.GET) &&
                !filePath.equals("default")) {
            return staticPageRouter.routeStaticPage(httpRequest, filePath);
        }

        return dynamicPageRouter.routeDynamicPage(httpRequest);
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
