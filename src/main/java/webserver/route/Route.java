package webserver.route;

import db.Database;
import exception.CustomErrorType;
import exception.CustomException;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;
import webserver.response.ResponseHandler;
import webserver.type.HttpStatusCode;
import webserver.type.MIMEType;
import webserver.utils.PropertyUtils;
import webserver.utils.URLUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

import static webserver.utils.PropertyUtils.loadStaticSourcePathFromProperties;

public class Route {
    private static final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);

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

    private final Map<String, Function<HttpRequest, HttpResponse>> REQUEST_MAPPING = Map.of(
            "static", this::getStaticPage,
            "/user/create", this::createUser
    );

    private static final Route instance = new Route();

    private Route() {

    }

    public static Route getInstance() {
        return instance;
    }

    public HttpResponse route(HttpRequest httpRequest) {
        Function<HttpRequest, HttpResponse> method =
                REQUEST_MAPPING.get(
                        REQUEST_MAPPING.keySet().stream()
                                .filter(key -> key.equals(URLUtils.getPath(httpRequest.getURL())))
                                .findFirst()
                                .orElse("static")
                );

        return method.apply(httpRequest);
    }

    private HttpResponse createUser(HttpRequest httpRequest) {
        String query = URLUtils.getQuery(httpRequest.getURL());
        Map<String, String> queryParams = URLUtils.configureQuery(query);

        // 이미 존재하는 유저인지 확인
        User findUser = Database.findUserById(queryParams.get("userId"));
        if (findUser != null) {
            throw new CustomException(CustomErrorType.USER_ALREADY_EXISTS);
        }

        // TODO: 모든 항목이 들어왔는지 확인
        User user = new User(queryParams.get("userId"), queryParams.get("password"),
                queryParams.get("nickname"), queryParams.get("email"));

        Database.addUser(user);

        logger.info("user created successfully! - userId : {}", user.getUserId());

        final String welcomePage = "/registration/welcome.html";
        return HttpResponse
                .status(httpRequest.getVersion(), HttpStatusCode.FOUND)
                .location(welcomePage)
                .build();
    }

    private HttpResponse getStaticPage(HttpRequest httpRequest) {
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
            throw new CustomException(CustomErrorType.SERVER_ERROR, e);
        }
    }

    private String getFilePath(String path) {
        // 파일 요청이면 그대로 리턴
        if (!URLUtils.getExtension(path).isEmpty()) {
            return STATIC_SOURCE_PATH.concat(path);
        }

        // 해당 url 매핑이 있는지 확인
        String resourcePath = URLUtils.getResourcePath(path);
        String requestPath = STATIC_MAPPING.keySet().stream()
                .filter(key -> key.equals(resourcePath))
                .findAny()
                .orElse("default");

        if (requestPath.equals("default")) {
            throw new CustomException(CustomErrorType.PATH_NOT_FOUND);
        }

        return STATIC_SOURCE_PATH.concat(path)
                .concat("/")    // 구분자
                .concat(STATIC_MAPPING.get(requestPath));
    }

}
