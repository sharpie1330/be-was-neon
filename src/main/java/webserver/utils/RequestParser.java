package webserver.utils;

import webserver.exception.request.MalformedBodyFormatException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestParser {
    private RequestParser() {

    }

    // TODO: value가 ,으로 구분된 경우 고려
    public static Map<String, List<String>> parseHeader(List<String> requestHeader) {
        Map<String, List<String>> headerMap = new HashMap<>();

        for (String headerLine : requestHeader) {
            String[] keyValue = headerLine.split(Delimiter.COLON, 2);
            String headerKey = keyValue[0].trim();
            List<String> headerValues = new ArrayList<>();
            for (String headerValue : keyValue[1].split(Delimiter.SEMICOLON)) {
                headerValues.add(headerValue.trim());
            }
            headerMap.put(headerKey, headerValues);
        }

        return headerMap;
    }

    public static Map<String, String> parseBody(byte[] body) {
        if (body == null) {
            throw new IllegalArgumentException("요청 바디가 존재하지 않습니다.");
        }

        String bodyString = new String(body, StandardCharsets.UTF_8);

        Map<String, String> bodyMap = new HashMap<>();

        for (String entry : bodyString.split(Delimiter.AMPERSAND)) {
            String[] keyValue = entry.split(Delimiter.EQUAL, 2);
            String key = keyValue[0];

            if (keyValue.length != 2 || key.isBlank()) {
                throw new MalformedBodyFormatException();
            }

            String value = keyValue[1];
            bodyMap.put(key, value);
        }

        return bodyMap;
    }
}
