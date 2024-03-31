package webserver.request;

import webserver.common.HttpBody;

import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.function.Function;

public interface HttpRequestBodyConverter {

    Map<Class<?>, Function<String, ?>> converters = Map.of(
            String.class, s -> s,
            Integer.class, Integer::parseInt,
            int.class, Integer::parseInt,
            Double.class, Double::parseDouble,
            double.class, Double::parseDouble,
            Boolean.class, Boolean::parseBoolean,
            boolean.class, Boolean::parseBoolean,
            Long.class, Long::parseLong,
            long.class, Long::parseLong,
            Float.class, Float::parseFloat
    );

    default Object convertValue(String fieldValueString, Class<?> fieldType) {
        Function<String, ?> converter = converters.get(fieldType);
        if (converter != null) {
            return converter.apply(fieldValueString);
        }

        throw new IllegalArgumentException("Unsupported field type: " + fieldType.getName());
    }

    Object convert(HttpBody httpBody, Parameter parameter) throws Exception;

    Map<String, String> parseBody(HttpBody httpBody);
}
