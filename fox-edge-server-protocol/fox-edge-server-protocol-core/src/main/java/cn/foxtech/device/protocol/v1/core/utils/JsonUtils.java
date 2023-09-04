package cn.foxtech.device.protocol.v1.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

public class JsonUtils {
    /**
     * 将T1的数据填入T2结构中
     * @param value 源数据
     * @param valueType 目的数据类型
     * @return 目的数据
     * @param <T1> 源数据类型
     * @param <T2> 目的数据类型
     * @throws IOException 异常信息
     */
    public static <T1, T2> T2 buildObject(T1 value, Class<T2> valueType) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsn = objectMapper.writeValueAsString(value);
        return objectMapper.readValue(jsn, valueType);
    }

    /**
     * 转换对象
     * @param value 源数据的数值
     * @param valueType 目的数据的类型
     * @return 目的数据
     * @param <T> 目的数据的类型
     * @throws IOException 异常信息
     */
    public static <T> T buildObject(String value, Class<T> valueType) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(value, valueType);
    }


    /**
     * 将MAP的数据填入T的各个字段中
     *
     * @param map 哈希表对象
     * @param valueType 目的数据类型
     * @return 目的数据
     * @param <T> 目的数据类型
     * @throws IOException 失败异常
     */
    public static <T> T buildObject(Map map, Class<T> valueType) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsn = objectMapper.writeValueAsString(map);
        return objectMapper.readValue(jsn, valueType);
    }

    /**
     * 将map转换为对象
     * @param map 源数据
     * @param valueType 目的类型
     * @return 目的数据
     * @param <T> 目的类型
     */
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
     * @param value 源数据
     * @return json
     * @param <T> 源数据的类型
     * @throws JsonProcessingException 失败异常
     */
    public static <T> String buildJson(T value) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(value);
        return json;
    }

    /**
     * 转换为json
     * @param value 源数据
     * @return json
     * @param <T> 源数据类型
     */
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
