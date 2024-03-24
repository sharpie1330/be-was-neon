package webserver.route.staticPage;

import exception.CustomException;
import exception.server.PathNotFoundException;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;
import webserver.type.HttpStatusCode;
import webserver.type.MIMEType;
import webserver.utils.PropertyUtils;
import webserver.utils.URLUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import static webserver.utils.PropertyUtils.loadStaticSourcePathFromProperties;

public class StaticPageRequestManager {
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

    public StaticPageRequestManager() {

    }

    public HttpResponse getStaticPage(HttpRequest httpRequest) {
        String filePath = getFilePath(URLUtils.getPath(httpRequest.getURL()));
        File file = new File(filePath);

        try (FileInputStream fileIn = new FileInputStream(file)) {
            byte[] body = new byte[(int) file.length()];
            int readLen = fileIn.read(body);
            MIMEType mimeType = MIMEType.getMimeType(URLUtils.getExtension(filePath));
            return HttpResponse
                    .ok(httpRequest.getVersion())
                    .contentLength(readLen)
                    .contentType(mimeType)
                    .body(body);
        } catch (IOException e) {
            throw new CustomException(HttpStatusCode.INTERNAL_SERVER_ERROR, e);
        }
    }

    private String getFilePath(String path) {
        // 파일 요청이면 그대로 리턴
        if (!URLUtils.getExtension(path).isEmpty()) {
            return STATIC_SOURCE_PATH.concat(path);
        }

        // 해당 url 매핑이 있는지 확인
        String requestPath = STATIC_MAPPING.keySet().stream()
                .filter(key -> key.equals(path))
                .findAny()
                .orElse("default");

        if (requestPath.equals("default")) {
            throw new PathNotFoundException();
        }

        return STATIC_SOURCE_PATH.concat(path)
                .concat("/")    // 구분자
                .concat(STATIC_MAPPING.get(requestPath));
    }
}
