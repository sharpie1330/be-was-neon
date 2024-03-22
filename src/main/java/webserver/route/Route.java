package webserver.route;

import exception.CustomErrorType;
import exception.CustomException;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;
import webserver.route.requestMapping.RequestMapping;
import webserver.route.requestMapping.RequestMappingFinder;
import webserver.route.user.requestManager.UserRequestManager;
import webserver.type.HttpMethod;
import webserver.type.MIMEType;
import webserver.utils.PropertyUtils;
import webserver.utils.URLUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static webserver.utils.PropertyUtils.loadStaticSourcePathFromProperties;

public class Route {
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

    private static final List<Class<?>> RequestManagerClasses =
            List.of(UserRequestManager.class);

    private static final Map<String, RequestMappingFinder> REQUEST_MAPPING_MAP = new HashMap<>();

    private static final Route instance = new Route();

    private Route() {
        init();
    }

    public static Route getInstance() {
        return instance;
    }

    // java reflection 활용, 요청 url path와 메서드 매칭
    private void init() {
        // RequestManager 클래스들에 대해서만
        for (Class<?> clazz : RequestManagerClasses) {
            RequestMappingFinder requestMappingFinder = new RequestMappingFinder(clazz);
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                //RequestMapping 어노테이션이 붙은 메서드만
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                    HttpMethod httpMethod = annotation.method();
                    String[] paths = annotation.path();

                    // path로 매칭된 값이 있는지 조회할 수 있도록 하는 과정
                    // e.g. /user/create => POST, createUser() 매핑
                    for (String path : paths) {
                        // 해당 path가 이미 map에 있으면
                        if (REQUEST_MAPPING_MAP.containsKey(path)) {
                            REQUEST_MAPPING_MAP.get(path).set(path, httpMethod, method);
                        } else {
                            requestMappingFinder.set(path, httpMethod, method);
                            REQUEST_MAPPING_MAP.put(path, requestMappingFinder);
                        }
                    }
                }
            }
        }
    }

    public HttpResponse route(HttpRequest httpRequest) throws Exception{
        HttpMethod httpMethod = httpRequest.getHttpMethod();
        String path = URLUtils.getPath(httpRequest.getURL());

        // url path에 대해 매핑된 http method, method 조회
        RequestMappingFinder requestMappingFinder = REQUEST_MAPPING_MAP.get(path);

        // 매핑 정보가 있으면
        if (requestMappingFinder != null) {
            Class<?> clazz = requestMappingFinder.getClazz();
            Object o = clazz.getDeclaredConstructor().newInstance();

            Method methodInfo = requestMappingFinder.getMethod(path);
            HttpMethod httpMethodInfo = requestMappingFinder.getHttpMethod(path);

            // 매핑된 메서드가 있고, 요청의 httpMethod가 일치하면 해당 메서드 실행(httpRequest 전달)
            if (methodInfo != null && httpMethod.equals(httpMethodInfo)) {
                return (HttpResponse) methodInfo.invoke(o, httpRequest);
            }

            // 매핑 정보와 일치하지 않으면 예외 발생
            throw new CustomException(CustomErrorType.INVALID_REQUEST_METHOD);
        }

        // 정적 페이지 처리
        return getStaticPage(httpRequest);
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
        String requestPath = STATIC_MAPPING.keySet().stream()
                .filter(key -> key.equals(path))
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
