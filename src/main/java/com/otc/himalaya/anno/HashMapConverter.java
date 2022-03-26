package com.otc.himalaya.anno;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.util.Map;

@Slf4j
public class HashMapConverter implements AttributeConverter<Map<String, Object>, String> {

    private static ObjectMapper om;

    @Override
    public String convertToDatabaseColumn(Map<String, Object> customerInfo) {

        String customerInfoJson = null;
        try {
            customerInfoJson = om.writeValueAsString(customerInfo);
        } catch (final JsonProcessingException e) {
            log.error("JSON writing error", e);
        }

        return customerInfoJson;
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String customerInfoJSON) {

        Map<String, Object> customerInfo = null;
        try {
            customerInfo = om.readValue(customerInfoJSON, Map.class);
        } catch (final IOException e) {
            log.error("JSON reading error", e);
        }

        return customerInfo;
    }

}
