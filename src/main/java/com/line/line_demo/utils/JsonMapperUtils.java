package com.line.line_demo.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.line.line_demo.config.kafka.MessageData;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;

@Slf4j
public class JsonMapperUtils {

    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
            .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, true);

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonMapperUtils.class);

    public static <O> String writeValueAsString(O o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return e.getMessage();
        }
    }

    public static <T> T convertJsonToObject(String jsonStr, final Class<T> clazz) {
        try {
            return mapper.readValue(jsonStr, clazz);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    public static <T> T convertJsonToObject(String jsonStr, final TypeReference<T> reference) {
        try {
            return mapper.readValue(jsonStr, reference);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    public static <T> T fromJson(String json, Class<?> parentType, Class<?>... parameterClasses) {
        if (json == null) {
            return null;
        }

        T object = null;
        try {
            var objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.registerModule(new JavaTimeModule());
            JavaType type =
                    objectMapper.getTypeFactory().constructParametricType(parentType, parameterClasses);
            object = objectMapper.readValue(json, type);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return object;
    }

    public static String toJson(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new JavaTimeModule());
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Can't build json from object", e);
        }
        return jsonString;
    }

}
