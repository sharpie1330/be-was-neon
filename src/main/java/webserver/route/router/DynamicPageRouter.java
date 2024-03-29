package webserver.route.router;

import webserver.exception.common.ServerException;
import webserver.exception.server.InternalServerErrorException;
import webserver.exception.server.MethodNotAllowedException;
import webserver.exception.request.PathNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;
import webserver.annotation.requestMapping.RequestMapping;
import webserver.annotation.requestMapping.RequestMappingFinder;
import webserver.route.user.requestManager.UserRequestManager;
import webserver.type.HttpMethod;
import webserver.utils.URLUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicPageRouter {
    private static final Logger logger = LoggerFactory.getLogger(DynamicPageRouter.class);

    private static final List<Class<?>> RequestManagerClasses =
            List.of(UserRequestManager.class);

    private static final Map<String, RequestMappingFinder> REQUEST_MAPPING_MAP = new HashMap<>();

    private static final DynamicPageRouter instance = new DynamicPageRouter();

    private DynamicPageRouter() {
        init();
    }

    public static DynamicPageRouter getInstance() {
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

    public HttpResponse routeDynamicPage(HttpRequest httpRequest) throws Exception{
        HttpMethod httpMethod = httpRequest.getRequestLine().getHttpMethod();
        String path = URLUtils.getPath(httpRequest.getRequestLine().getURL());

        // url path에 대해 매핑된 http method, method 조회
        RequestMappingFinder requestMappingFinder = REQUEST_MAPPING_MAP.get(path);

        // 매핑 정보가 있으면
        if (requestMappingFinder != null) {
            try {
                Class<?> clazz = requestMappingFinder.getClazz();
                Object o = clazz.getDeclaredConstructor().newInstance();

                Method methodInfo = requestMappingFinder.getMethod(path);
                HttpMethod httpMethodInfo = requestMappingFinder.getHttpMethod(path);

                // 매핑된 메서드가 있고, 요청의 httpMethod가 일치하면 해당 메서드 실행(httpRequest 전달)
                if (methodInfo != null && httpMethod.equals(httpMethodInfo)) {
                    return (HttpResponse) methodInfo.invoke(o, httpRequest);
                }

                // 매핑 정보와 일치하지 않으면 예외 발생
                throw new MethodNotAllowedException();
            } catch (InvocationTargetException e) {
                Throwable targetException = e.getTargetException();
                if (targetException instanceof ServerException) {
                    throw (ServerException) targetException;
                }
                throw (Exception) targetException;
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                throw new InternalServerErrorException(e.getMessage(), e);
            }
        }

        throw new PathNotFoundException();
    }
}
