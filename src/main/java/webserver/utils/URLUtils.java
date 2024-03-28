package webserver.utils;

import webserver.exception.request.MalformedUrlFormatException;

import java.util.HashMap;
import java.util.Map;

public class URLUtils {
    private static final String DOT = ".";
    private static final String SLASH = "/";
    private static final String EMPTY_STRING = "";
    private static final String AMPERSAND = "&";
    private static final String EQUAL = "=";
    private static final String QUESTION_MARK = "?";

    public static String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(DOT);
        if (dotIndex == -1) {
            return "";
        }
        return fileName.substring(dotIndex + 1);
    }

    public static boolean isQueryExists(String requestUrl) {
        return requestUrl.lastIndexOf(QUESTION_MARK) != -1;
    }

    public static String getPath(String requestUrl) {
        int index = requestUrl.lastIndexOf(QUESTION_MARK);
        if (index != -1) {
            return requestUrl.substring(0, index);
        }
        return requestUrl;
    }

    public static String getQuery(String requestUrl) {
        int firstIndex = requestUrl.indexOf(QUESTION_MARK);
        int lastIndex = requestUrl.lastIndexOf(QUESTION_MARK);

        // ?가 하나만 있지 않으면 오류
        if (firstIndex != lastIndex) {
            throw new MalformedUrlFormatException();
        }

        if (firstIndex != -1) {
            return requestUrl.substring(firstIndex + 1);
        }
        return "";
    }

    public static Map<String, String> configureQuery(String query) {
        Map<String, String> queryParamMap = new HashMap<>();

        // 쿼리가 없으면 오류
        if (query.isBlank()) {
            throw new IllegalArgumentException("요청 파라미터가 존재하지 않습니다.");
        }

        String[] queryStrings = query.split(AMPERSAND);

        for (String queryParam : queryStrings) {
            String[] keyValue = queryParam.split(EQUAL, -1);

            // =을 기준으로 나눈 개수가 2가 아니거나 key가 없으면 오류
            if (keyValue.length != 2 || keyValue[0].isBlank()) {
                throw new MalformedUrlFormatException();
            }

            String key = keyValue[0];
            String value = keyValue[1];
            queryParamMap.put(key, value);
        }

        return queryParamMap;
    }
}
