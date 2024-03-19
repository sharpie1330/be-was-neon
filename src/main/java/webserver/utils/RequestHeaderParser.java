package webserver.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHeaderParser {
    private RequestHeaderParser() {

    }

    // TODO: value가 ,으로 구분된 경우 고려
    public static Map<String, List<String>> parse(List<String> requestHeader) {
        Map<String, List<String>> headerMap = new HashMap<>();

        for(String headerLine : requestHeader) {
            String[] keyValue = headerLine.split(":", 2);
            String headerKey = keyValue[0].trim();
            List<String> headerValues = new ArrayList<>();
            for (String headerValue : keyValue[1].split(";")) {
                headerValues.add(headerValue.trim());
            }
            headerMap.put(headerKey, headerValues);
        }

        return headerMap;
    }
}
