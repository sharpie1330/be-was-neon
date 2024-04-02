package webserver.route;

import webserver.annotation.Authorize;
import webserver.annotation.RequestBody;
import webserver.exception.request.PathNotFoundException;
import webserver.exception.request.UnsupportedContentTypeException;
import webserver.http.type.HttpHeader;
import webserver.http.type.HttpRequest;
import webserver.http.type.HttpResponse;
import webserver.annotation.RequestMapping;
import webserver.http.type.MIMEType;
import webserver.session.Cookie;
import webserver.utils.Delimiter;
import webserver.utils.HttpRequestBodyConverter;
import webserver.utils.UrlEncodedFormatConverter;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
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

                        Parameter requestBodyParameter = findAnnotatedParam(method, RequestBody.class);
                        Parameter authorizeParameter = findAnnotatedParam(method, Authorize.class);

                        List<Object> args = new ArrayList<>(2);

                        if (authorizeParameter != null) {
                            Cookie cookie = configureCookie(httpRequest.getHeader().getOrDefault(HttpHeader.COOKIE, Collections.emptyList()));
                            args.add(cookie);
                        }

                        if (requestBodyParameter != null) {
                            HttpRequestBodyConverter httpRequestBodyConverter = resolveConverter(httpRequest);
                            Object convert = httpRequestBodyConverter.convert(httpRequest.getBody(), requestBodyParameter);
                            args.add(convert);
                        }

                        return (HttpResponse) method.invoke(o, args.toArray());
                    } catch (InvocationTargetException e) {
                        Throwable targetException = e.getTargetException();
                        throw (Exception) targetException;
                    }
                }
            }
        }

        throw new PathNotFoundException();
    }

    private Cookie configureCookie(List<String> cookies) {
        Cookie cookie = new Cookie();
        for (String string : cookies) {
            String[] kv = string.trim().split(Delimiter.EQUAL, 2);
            cookie.setCookie(kv[0], kv[1]);
        }

        return cookie;
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

    private Parameter findAnnotatedParam(Method method, Class<? extends Annotation> clazz) {
        Parameter[] parameters = method.getParameters();

        for (Parameter parameter : parameters) {
            if (parameter.isAnnotationPresent(clazz)) {
                return parameter;
            }
        }
        return null;
    }
}
