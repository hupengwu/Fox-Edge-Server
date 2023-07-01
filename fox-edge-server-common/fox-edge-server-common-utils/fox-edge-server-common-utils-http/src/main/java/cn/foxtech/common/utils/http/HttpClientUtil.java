package cn.foxtech.common.utils.http;

import cn.foxtech.common.constant.HttpStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class HttpClientUtil {

    /**
     * 执行GET方法
     *
     * @param url   URL
     * @param param 路径参数
     * @return
     */
    public static String executeGet(String url, Map<String, String> param) {

        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();

        String resultString = "";
        CloseableHttpResponse response = null;
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key));
                }
            }
            URI uri = builder.build();

            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);

            // 执行请求
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == HttpStatus.SUCCESS) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }

    /**
     * 执行GET
     *
     * @param url 路径参数
     * @return
     */
    public static String executeGet(String url) {
        Map<String, String> param = new HashMap<>();
        return executeGet(url, param);
    }


    public static String executePost(String url, String json, Map<String, String> header) throws IOException {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            for (String key : header.keySet()) {
                httpPost.setHeader(key, header.get(key));
            }

            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return resultString;
    }


    /**
     * 执行POST方法
     *
     * @param url
     * @param json
     * @return
     */
    public static String executePost(String url, String json) throws IOException {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return resultString;
    }

    public static <REQ, RSP> RSP executePost(String url, REQ requestVO, Class<RSP> rspClass) throws IOException {
        // 转换成json
        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(requestVO);

        // 向通道服务器发送请求
        String respondJson = HttpClientUtil.executePost(url, requestJson);

        // 转换JSON结构
        RSP respondVO = objectMapper.readValue(respondJson, rspClass);
        return respondVO;
    }

    public static <RSP> RSP executeGet(String url, Class<RSP> rspClass) throws IOException {
        // 转换成json
        ObjectMapper objectMapper = new ObjectMapper();

        // 向通道服务器发送请求
        String respondJson = HttpClientUtil.executeGet(url);

        // 转换JSON结构
        RSP respondVO = objectMapper.readValue(respondJson, rspClass);
        return respondVO;
    }

    public static void main(String[] args) throws IOException {
        String pass = "{\"username\":\"admin\",\"password\":\"admin123\"}";
        String result1 = HttpClientUtil.executePost("http://120.25.241.120:8080/auth/login", pass);

        Map<String, String> header = new HashMap<>();
        header.put("Authorization","Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyX2lkIjoxLCJ1c2VyX2tleSI6ImVkZmYwY2I0LTI3NTctNDUyZS05MjFkLTcyNTYyNTQ2MjFmMiIsInVzZXJuYW1lIjoiYWRtaW4ifQ.VkVh8C5bRU-MwvQZIy6PSe9VRBJR-2mNcEex0jboikrLzwyCbwmmCRwkqiAFopx1LCNIQFAZHLX5_pZueem-Wg");

        String json = "{\n" + "    \"edgeId\" : \"BFEBFBFF000906A3\",\n" + "    \"entityTypeList\" : [\"DeviceValueEntity\"]\n" + "}";

        String result = HttpClientUtil.executePost("http://120.25.241.120:8080/aggregator/config/timestamp", json,header);
        result = HttpClientUtil.executePost("http://120.25.241.120:8080/aggregator/config/timestamp", json,header);
        result = HttpClientUtil.executePost("http://120.25.241.120:8080/aggregator/config/timestamp", json,header);

    }
}
