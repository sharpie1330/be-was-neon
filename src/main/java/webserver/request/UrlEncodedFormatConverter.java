package webserver.request;

import webserver.common.HttpBody;
import webserver.exception.request.MalformedBodyFormatException;
import webserver.utils.Delimiter;

import java.lang.reflect.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UrlEncodedFormatConverter implements HttpRequestBodyConverter{

    public UrlEncodedFormatConverter() {

    }

    @Override
    public Object convert(HttpBody httpBody, Parameter parameter) throws Exception {
        Map<String, String> parsedBody = parseBody(httpBody);

        Class<?> type = parameter.getType();
        boolean validAnnotationPresent = isValidAnnotationPresent(parameter);

        List<Class<?>> fieldTypes = new ArrayList<>();
        List<Object> fieldValues = new ArrayList<>();
        for (Field field : type.getDeclaredFields()) {
            String fieldValue = URLDecoder.decode(parsedBody.get(field.getName()), StandardCharsets.UTF_8);

            if (validAnnotationPresent) {
                checkValid(field, fieldValue);
            }

            Class<?> fieldType = field.getType();
            fieldTypes.add(fieldType);
            fieldValues.add(convertValue(fieldValue, fieldType));
        }


        Constructor<?> allArgsConstructor = type.getDeclaredConstructor(fieldTypes.toArray(new Class<?>[0]));
        return allArgsConstructor.newInstance(fieldValues.toArray());
    }

    @Override
    public Map<String, String> parseBody(HttpBody httpBody) {
        byte[] body = httpBody.getBody();

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
