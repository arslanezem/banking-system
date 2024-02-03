package org.exercise.parsers;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class for parsing JSON data.
 */
public class JsonParser {
    private static JsonParser instance;

    private JsonParser() {
    }

    /**
     * Method to get the single instance of JsonParser.
     *
     * @return The JsonParser instance.
     */
    public static synchronized JsonParser getInstance() {
        if (instance == null) {
            instance = new JsonParser();
        }
        return instance;
    }

    /**
     * Converts an object to its JSON representation.
     *
     * @param object The object to convert to JSON.
     * @return The JSON representation of the object.
     * @throws Exception If an error occurs during the conversion.
     */
    public static String convertObjectToJson(Object object) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

    /**
     * Converts a JSON string to an object of the specified type.
     *
     * @param jsonString The JSON string to convert to an object.
     * @param valueType  The class of the object to be created.
     * @param <T>        The type of the object.
     * @return The object created from the JSON string.
     * @throws Exception If an error occurs during the conversion.
     */
    public static <T> T convertJsonToObject(String jsonString, Class<T> valueType) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonString, valueType);
    }

}
