package webserver.utils;

import webserver.annotation.CheckValid;
import webserver.annotation.Valid;
import webserver.exception.server.BadRequestException;
import webserver.http.type.HttpBody;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    default boolean isValidAnnotationPresent(Parameter parameter) {
        return parameter.isAnnotationPresent(Valid.class);
    }

    default void checkValid(Field field, String value) {
        if (!field.isAnnotationPresent(CheckValid.class)) {
            return;
        }

        CheckValid annotation = field.getAnnotation(CheckValid.class);
        Pattern pattern = Pattern.compile(annotation.regex());
        Matcher matcher = pattern.matcher(value);
        boolean matches = matcher.matches();

        if (annotation.included() != matches) {
            throw new BadRequestException(annotation.message() != null ? annotation.message() : "Invalid Request Data");
        }
    }

    Object convert(HttpBody httpBody, Parameter parameter) throws Exception;

    Map<String, String> parseBody(HttpBody httpBody);
}
