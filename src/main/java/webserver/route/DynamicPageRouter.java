package webserver.route;

import webserver.annotation.RequestBody;
import webserver.exception.request.PathNotFoundException;
import webserver.exception.request.UnsupportedContentTypeException;
import webserver.http.type.HttpRequest;
import webserver.http.type.HttpResponse;
import webserver.annotation.RequestMapping;
import webserver.http.type.MIMEType;
import webserver.utils.HttpRequestBodyConverter;
import webserver.utils.UrlEncodedFormatConverter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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

                        Parameter parameter = findRequestBodyParam(method);
                        HttpRequestBodyConverter httpRequestBodyConverter = resolveConverter(httpRequest);
                        if (parameter != null) {
                            Object convert = httpRequestBodyConverter.convert(httpRequest.getBody(), parameter);
                            return (HttpResponse) method.invoke(o, convert);
                        }
                        return (HttpResponse) method.invoke(o);
                    } catch (InvocationTargetException e) {
                        Throwable targetException = e.getTargetException();
                        throw (Exception) targetException;
                    }
                }
            }
        }

        throw new PathNotFoundException();
    }

    private HttpRequestBodyConverter resolveConverter(HttpRequest httpRequest) {
        MIMEType contentType = httpRequest.getHeader().getContentType();
        if (contentType.equals(MIMEType.urlencoded)) {
            return new UrlEncodedFormatConverter();
        }
        throw new UnsupportedContentTypeException();
    }

    private boolean isMatched(Method method, String path) {
        if (method.isAnnotationPresent(RequestMapping.class)) {
            for (String mappedUrlPath : method.getAnnotation(RequestMapping.class).path()) {
                if (mappedUrlPath.startsWith(path)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Parameter findRequestBodyParam(Method method) {
        Parameter[] parameters = method.getParameters();

        for (Parameter parameter : parameters) {
            if (parameter.isAnnotationPresent(RequestBody.class)) {
                return parameter;
            }
        }
        return null;
    }
}
