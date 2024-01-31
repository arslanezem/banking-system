package org.exercise.parsers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.exercise.model.Message;

public class JsonParser {
    private static JsonParser instance;

    private JsonParser() {
    }

    public static synchronized JsonParser getInstance() {
        if (instance == null) {
            instance = new JsonParser();
        }
        return instance;
    }

    public static String convertObjectToJson(Object object) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

    public static <T> T convertJsonToObject(String jsonString, Class<T> valueType) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonString, valueType);
    }

}
