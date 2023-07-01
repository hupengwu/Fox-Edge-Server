package cn.foxtech.common.utils.json;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

public class JsonUtils {
    /**
     * 将T1的数据填入T2结构中
     *
     * @param value
     * @param valueType
     * @param <T1>
     * @param <T2>
     * @return
     */
    public static <T1, T2> T2 buildObject(T1 value, Class<T2> valueType) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsn = objectMapper.writeValueAsString(value);
        return objectMapper.readValue(jsn, valueType);
    }

    public static <T> T buildObject(String value, Class<T> valueType) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(value, valueType);
    }

    public static <T> T buildObjectWithoutException(String value, Class<T> valueType) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(value, valueType);
        } catch (Exception e) {
            return null;
        }
    }

    public static Map<String, Object> buildMapWithDefault(String value, Map<String, Object> defaultValue) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(value, Map.class);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 将MAP的数据填入T的各个字段中
     *
     * @param map
     * @param valueType
     * @param <T>
     * @return
     */
    public static <T> T buildObject(Map map, Class<T> valueType) throws JsonParseException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsn = objectMapper.writeValueAsString(map);
            return objectMapper.readValue(jsn, valueType);
        } catch (JsonProcessingException je) {
            throw new JsonParseException(null, je.getMessage());
        } catch (IOException ie) {
            throw new JsonParseException(null, ie.getMessage());
        }
    }

    public static <T> T buildObjectWithoutException(Map map, Class<T> valueType) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsn = objectMapper.writeValueAsString(map);
            return objectMapper.readValue(jsn, valueType);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 生成JSON字符串
     *
     * @param value
     * @param <T>
     * @return
     */
    public static <T> String buildJson(T value) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(value);
        return json;
    }

    public static <T> String buildJsonWithoutException(T value) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(value);
            return json;
        } catch (Exception e) {
            return null;
        }
    }
}
