package cn.foxtech.common.utils.http;

import cn.foxtech.common.constant.HttpStatus;
import cn.foxtech.core.exception.ServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
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
    private static final ObjectMapper objectMapper = new ObjectMapper();

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

    public static String executeGet(String url, Map<String, String> param, Map<String, String> header) {
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

            // 创建请求内容
            for (String key : header.keySet()) {
                httpGet.setHeader(key, header.get(key));
            }


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

    public static String executePut(String url, String json, Map<String, String> header) throws IOException {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Put请求
            HttpPut httpPut = new HttpPut(url);
            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPut.setEntity(entity);
            for (String key : header.keySet()) {
                httpPut.setHeader(key, header.get(key));
            }

            // 执行http请求
            response = httpClient.execute(httpPut);
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

    public static String executePut(String url, String json) throws IOException {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Put
            HttpPut httpPut = new HttpPut(url);
            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPut.setEntity(entity);

            // 执行http请求
            response = httpClient.execute(httpPut);
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
        String requestJson = objectMapper.writeValueAsString(requestVO);

        // 向通道服务器发送请求
        String respondJson = HttpClientUtil.executePost(url, requestJson);

        try {
            // 转换JSON结构
            RSP respondVO = objectMapper.readValue(respondJson, rspClass);
            return respondVO;
        } catch (Exception e) {
            throw new ServiceException("服务端响应异常：" + respondJson);
        }

    }

    public static <REQ, RSP> RSP executePut(String url, REQ requestVO, Class<RSP> rspClass) throws IOException {
        // 转换成json
        String requestJson = objectMapper.writeValueAsString(requestVO);

        // 向通道服务器发送请求
        String respondJson = HttpClientUtil.executePut(url, requestJson);

        try {
            // 转换JSON结构
            RSP respondVO = objectMapper.readValue(respondJson, rspClass);
            return respondVO;
        } catch (Exception e) {
            throw new ServiceException("服务端响应异常：" + respondJson);
        }

    }

    public static <RSP> RSP executeGet(String url, Class<RSP> rspClass) throws IOException {
        // 向通道服务器发送请求
        String respondJson = HttpClientUtil.executeGet(url);

        try {
            // 转换JSON结构
            RSP respondVO = objectMapper.readValue(respondJson, rspClass);
            return respondVO;
        } catch (Exception e) {
            throw new ServiceException("服务端响应异常：" + respondJson);
        }
    }


}
