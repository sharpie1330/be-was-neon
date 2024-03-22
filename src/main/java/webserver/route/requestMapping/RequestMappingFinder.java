package webserver.route.requestMapping;

import webserver.type.HttpMethod;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RequestMappingFinder {
    private final Class<?> clazz;
    private final Map<String, RequestMappingInfo> infos;

    public RequestMappingFinder(Class<?> clazz) {
        this.clazz = clazz;
        this.infos = new HashMap<>(2);
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void set(String path, HttpMethod httpMethod, Method method) {
        infos.put(path, new RequestMappingInfo(httpMethod, method));
    }

    public HttpMethod getHttpMethod(String path) {
        RequestMappingInfo requestMappingInfo = infos.get(path);
        return requestMappingInfo != null ? requestMappingInfo.getHttpMethod() : null;
    }

    public Method getMethod(String path) {
        RequestMappingInfo requestMappingInfo = infos.get(path);
        return requestMappingInfo != null ? requestMappingInfo.getMethod() : null;
    }

    private static class RequestMappingInfo {
        private final HttpMethod httpMethod;
        private final Method method;

        public RequestMappingInfo(HttpMethod httpMethod, Method method) {
            this.httpMethod = httpMethod;
            this.method = method;
        }

        public HttpMethod getHttpMethod() {
            return httpMethod;
        }

        public Method getMethod() {
            return method;
        }
    }
}
