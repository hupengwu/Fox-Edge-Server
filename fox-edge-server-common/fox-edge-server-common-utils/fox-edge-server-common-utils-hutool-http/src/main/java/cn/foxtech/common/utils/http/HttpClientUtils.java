package cn.foxtech.common.utils.http;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

import java.util.HashMap;
import java.util.Map;

public class HttpClientUtils {
    public static void main(String[] args) {
        try {
            HttpResponse result3 = HttpClientUtils.executeRestful("http://localhost:9000/manager/system/channel/entity", "get");

            String json = "{\"name\":\"COM3\",\"send\":\"b0 01 00 fe fe\",\"timeout\":3000}";
            HttpClientUtils util = new HttpClientUtils();
            HttpResponse result = HttpClientUtils.executeRestful("http://localhost:9000/user/login?username=admin&password=12345678", "get");
//            List<HttpCookie> cookies = result.getCookies();
//            HttpCookie cookie = result.getCookie("token");
//            String value = cookie.getValue();
//            String body = result.body();
            HttpResponse result1 = HttpClientUtils.executeRestful("http://localhost:9000/manager/system/channel/entity", "get");
            String body1 = result1.body();
            body1 = result1.body();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static HttpResponse executeRestful(String url, String method) {
        Map<String, String> headers = new HashMap<>();
        return execute(url, method, headers, null);
    }

    public static HttpResponse executeRestful(String url, String method, String json) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return execute(url, method, headers, json);
    }

    public static HttpResponse executeRestful(String url, String method, Map<String, String> headers, String json) {
        return execute(url, method, headers, json);
    }

    public static HttpResponse execute(String url, String method, Map<String, String> headers, String body) {
        HttpRequest httpRequest = null;

        // 确定Method
        if ("post".equalsIgnoreCase(method)) {
            httpRequest = HttpRequest.post(url);
        }
        if ("put".equalsIgnoreCase(method)) {
            httpRequest = HttpRequest.put(url);
        }
        if ("get".equalsIgnoreCase(method)) {
            httpRequest = HttpRequest.get(url);
        }
        if ("delete".equalsIgnoreCase(method)) {
            httpRequest = HttpRequest.delete(url);
        }
        if ("patch".equalsIgnoreCase(method)) {
            httpRequest = HttpRequest.patch(url);
        }

        // 确定header
        if (headers != null && !headers.isEmpty()) {
            httpRequest = httpRequest.addHeaders(headers);
        }

        // 确定body
        if (body != null && !body.isEmpty()) {
            httpRequest = httpRequest.body(body);
        }

        // 发出请求
        return httpRequest.execute();
    }
}
