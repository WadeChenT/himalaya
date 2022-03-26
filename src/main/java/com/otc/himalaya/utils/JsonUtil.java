package com.otc.himalaya.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class JsonUtil {

    private static ObjectMapper om;

    public JsonUtil(ObjectMapper om) {
        JsonUtil.om = om;
    }

    /*
    static {
        om = new ObjectMapper()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                // .setDateFormat(new StdDateFormat().withColonInTimeZone(true))
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
    }
    */

    public static String toJsonUnFormatted(Object obj) throws JsonProcessingException {
        return om.writeValueAsString(obj);
    }

    public static String toJsonUnFormattedLog(Object obj) {
        try {
            return om.writeValueAsString(obj);
        } catch (Exception ex) {
            log.error("JsonUtil:", ex);
            return Objects.nonNull(obj) ? obj.toString() : "null";
        }
    }

    public static String toJsonLog(Object obj) {
        try {
            return om.writerWithDefaultPrettyPrinter()
                     .writeValueAsString(obj);
        } catch (Exception ex) {
            log.error("JsonUtil:", ex);
            return Objects.nonNull(obj) ? obj.toString() : "null";
        }
    }

    public static String toJson(Object obj) throws JsonProcessingException {
        return om.writerWithDefaultPrettyPrinter()
                 .writeValueAsString(obj);
    }

    public static Map<String, Object> readJsonAsMap(String json) throws IOException {
        return om.readValue(json, new TypeReference<>() {});
    }

    public static Map<String, Object> convertValue(Object obj) {
        return om.convertValue(obj, new TypeReference<>() {});
    }

    public static <T> T convertValue(Object obj, Class<T> clazz) {
        return om.convertValue(obj, clazz);
    }
}
