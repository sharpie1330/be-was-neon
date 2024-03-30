package webserver.route;

import webserver.exception.request.PathNotFoundException;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;
import webserver.annotation.RequestMapping;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class DynamicPageRouter {
    private final List<Class<?>> controllers;

    public DynamicPageRouter(List<Class<?>> controllers) {
        this.controllers = controllers;
    }

    public HttpResponse routeDynamicPage (HttpRequest httpRequest, String path) throws Exception{
        for (Class<?> controller : controllers) {
            for (Method method : controller.getMethods()) {
                if (isMatched(method, path)) {
                    try {
                        Object o = controller.getDeclaredConstructor().newInstance();
                        return (HttpResponse) method.invoke(o, httpRequest);
                    } catch (InvocationTargetException e) {
                        Throwable targetException = e.getTargetException();
                        throw (Exception) targetException;
                    }
                }
            }
        }

        throw new PathNotFoundException();
    }

    private boolean isMatched(Method method, String path) {
        if (method.isAnnotationPresent(RequestMapping.class)) {
            for (String mappedUrlPath : method.getAnnotation(RequestMapping.class).path()) {
                if (path.equals(mappedUrlPath)) {
                    return true;
                }
            }
        }
        return false;
    }
}
