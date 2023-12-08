package cn.foxtech.common.utils.http;


import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class UploadUtil {
    private static final ContentType STRING_CONTENT_TYPE = ContentType.create("text/plain", StandardCharsets.UTF_8);

    public static void main(String[] args) throws IOException {
        String auth = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyX2lkIjoxLCJ1c2VyX2tleSI6ImMyYWI4NzUxLWQ1NTktNDc1MC05NGExLWM0ZGEzMDM2MTEyYyIsInVzZXJuYW1lIjoiYWRtaW4ifQ.RQwi2P4QCwWavwtXNKc611phbToEsCstJ0gX_nXP__iDY8MrHkkF2OSP0TTuFnIRPZm8n3RD00IrD7Rpqm5UNA";

        File file = new File("D:\\我的项目\\fox-edge-server-protocol-gitee\\fox-edge-server-protocol\\fox-edge-server-protocol-mitsubishi-plc-fx-core\\target\\fox-edge-server-protocol-mitsubishi-plc-fx-core-1.0.7.jar");
        String url = "http://192.168.1.21/prod-api/manager/repository/component/upload";
        Map<String, String> headers = new HashMap<>();
        //   headers.put("Accept", "application/json");
        headers.put("Authorization", auth);

        Map<String, Object> formData = new HashMap<>();
        formData.put("modelType", "decoder");
        formData.put("modelName", "fox-edge-server-protocol-mitsubishi-plc-fx-core");
        formData.put("modelVersion", "v1");
        formData.put("component", "service");
        formData.put("file", file);
        UploadUtil.multipartPost(url, headers, formData);
    }

    /**
     * 文件上传
     *
     * @param url      文件上传的url
     * @param headers  服务器要求的header信息，比如token
     * @param formData 服务器要求携带的form-data信息
     * @return 执行结果
     */
    public static String multipartPost(String url, Map<String, String> headers, Map<String, Object> formData) {
        // 创建 HttpPost 对象
        HttpPost httpPost = new HttpPost(url);

        // 设置请求头
        for (String key : headers.keySet()) {
            httpPost.setHeader(key, headers.get(key));
        }

        // 使用 MultipartEntityBuilder 构造请求体
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        //设置浏览器兼容模式
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        //设置请求的编码格式
        builder.setCharset(Consts.UTF_8);
        // 设置 Content-Type
        builder.setContentType(ContentType.MULTIPART_FORM_DATA);
        for (Map.Entry<String, Object> entry : formData.entrySet()) {
            String key = entry.getKey();
            Object value = formData.get(key);
            // 添加请求参数
            addMultipartBody(builder, key, value);
        }
        HttpEntity entity = builder.build();
        // 将构造好的 entity 设置到 HttpPost 对象中
        httpPost.setEntity(entity);
        return execute(httpPost, null);
    }

    private static void addMultipartBody(MultipartEntityBuilder builder, String key, Object value) {
        if (value == null) {
            return;
        }
        // MultipartFile 是 spring mvc 接收到的文件。
        if (value instanceof File) {
            File file = (File) value;
            builder.addBinaryBody(key, file, ContentType.MULTIPART_FORM_DATA, file.getName());
        } else {
            // 使用 UTF_8 编码的 ContentType，否则可能会有中文乱码问题
            builder.addTextBody(key, value.toString(), STRING_CONTENT_TYPE);
        }
    }

    private static String execute(HttpRequestBase httpRequestBase, HttpContext context) {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 使用 try-with-resources 发起请求，保证请求完成后资源关闭
        try (CloseableHttpResponse httpResponse = httpClient.execute(httpRequestBase, context)) {
            // 处理响应体
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                String entityContent = EntityUtils.toString(httpEntity, Consts.UTF_8);
                return entityContent;
            }
        } catch (Exception ex) {
            throw new RuntimeException("http execute failed:" + ex.getMessage());
        }

        throw new RuntimeException("http execute failed:" + HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }
}