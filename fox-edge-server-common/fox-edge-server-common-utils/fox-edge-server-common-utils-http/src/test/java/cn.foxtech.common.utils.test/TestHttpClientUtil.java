package cn.foxtech.common.utils.test;

import cn.foxtech.common.utils.http.HttpClientUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TestHttpClientUtil {
    public static void main(String[] args) throws IOException {
        String pass = "{\"username\":\"admin\",\"password\":\"admin123\"}";
        String result1 = HttpClientUtil.executePost("http://120.25.241.120:8080/auth/login", pass);

        Map<String, String> header = new HashMap<>();
        header.put("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyX2lkIjoxLCJ1c2VyX2tleSI6ImVkZmYwY2I0LTI3NTctNDUyZS05MjFkLTcyNTYyNTQ2MjFmMiIsInVzZXJuYW1lIjoiYWRtaW4ifQ.VkVh8C5bRU-MwvQZIy6PSe9VRBJR-2mNcEex0jboikrLzwyCbwmmCRwkqiAFopx1LCNIQFAZHLX5_pZueem-Wg");

        String json = "{\n" + "    \"edgeId\" : \"BFEBFBFF000906A3\",\n" + "    \"entityTypeList\" : [\"DeviceValueEntity\"]\n" + "}";

        String result = HttpClientUtil.executePost("http://120.25.241.120:8080/aggregator/config/timestamp", json, header);
        result = HttpClientUtil.executePost("http://120.25.241.120:8080/aggregator/config/timestamp", json, header);
        result = HttpClientUtil.executePost("http://120.25.241.120:8080/aggregator/config/timestamp", json, header);

    }
}
