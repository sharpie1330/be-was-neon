package webserver.route;

import webserver.request.HttpRequest;
import webserver.response.HttpResponse;
import webserver.type.HttpMethod;
import webserver.utils.Delimiter;
import webserver.utils.URLUtils;

import java.io.File;
import java.util.List;

import static webserver.WebServer.STATIC_SOURCE_PATH;

public class Router {
    private static final String DYNAMIC_FLAG = "dynamic";
    private static final String INDEX_HTML = "index.html";

    private final StaticPageRouter staticPageRouter;
    private final DynamicPageRouter dynamicPageRouter;

    public Router(List<Class<?>> controllers) {
        this.dynamicPageRouter = new DynamicPageRouter(controllers);
        this.staticPageRouter = new StaticPageRouter();
    }

    public HttpResponse route(HttpRequest httpRequest) throws Exception{
        String path = URLUtils.getPath(httpRequest.getRequestLine().getURL());
        String filePath = getFilePath(path);

        // GET 요청이고, filePath 조회한 결과가 DYNAMIC_FLAG가 아닌 경우에만 정적 페이지 라우팅
        if (httpRequest.getRequestLine().getHttpMethod().equals(HttpMethod.GET) &&
                !filePath.equals(DYNAMIC_FLAG)) {
            return staticPageRouter.routeStaticPage(httpRequest, filePath);
        }

        return dynamicPageRouter.routeDynamicPage(httpRequest, path);
    }

    private String getFilePath(String path) {
        String filePath = STATIC_SOURCE_PATH + path;
        File file = new File(filePath);
        if (file.isDirectory()) {
            if (file.exists()) {
                return filePath.concat(Delimiter.SLASH + INDEX_HTML);
            }
            return DYNAMIC_FLAG;
        }
        return filePath;
    }

}
