package cn.foxtech.proxy.cloud.forwarder.service.proxy;

import cn.foxtech.common.constant.HttpStatus;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.proxy.cloud.forwarder.config.ApplicationConfig;
import cn.foxtech.proxy.cloud.forwarder.vo.RestfulLikeRequestVO;
import cn.foxtech.proxy.cloud.forwarder.vo.RestfulLikeRespondVO;
import cn.foxtech.common.utils.http.HttpClientUtils;
import cn.foxtech.core.exception.ServiceException;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP Restful API接口的服务转发代理，比如system和gateway服务
 */
@Component
public class HttpRestfulProxyService {
    @Getter(value = AccessLevel.PUBLIC)
    private final Map<String, String> proxyMapping = new HashMap<>();

    @Autowired
    private ApplicationConfig constant;


    public void Initialize() {
        // manager-system服务
        this.proxyMapping.put("/manager/system", "http://localhost:9000");
        // gateway的user服务
        this.proxyMapping.put("/user", "http://localhost:9000");
    }


    /**
     * 检查：是不是HttpProxy的资源
     *
     * @param resource 资源
     * @return 是否成功
     */
    public boolean isHttpResource(String resource) {
        return this.getHttpHost(resource) != null;
    }

    /**
     * 查询host
     *
     * @param resource 资源
     * @return 是否成功
     */
    private String getHttpHost(String resource) {
        for (String head : this.proxyMapping.keySet()) {
            if (resource.startsWith(head + "/")) {
                return this.proxyMapping.get(head);
            }
        }

        return null;
    }

    /**
     * 执行操作
     *
     * @param requestVO 请求报文
     * @return 响应报文
     */
    public RestfulLikeRespondVO execute(RestfulLikeRequestVO requestVO) {
        String host = this.getHttpHost(requestVO.getResource());
        if (host == null) {
            throw new ServiceException("尚未支持的方法");
        }

        try {
            // 执行restful请求
            String method = requestVO.getMethod();
            String json = JsonUtils.buildJson(requestVO.getBody());
            Map body = this.executeRestful(host, requestVO.getResource(), method, json);

            // 返回操作结果
            RestfulLikeRespondVO respondVO = new RestfulLikeRespondVO();
            respondVO.bindVO(requestVO);
            respondVO.setBody(body);
            return respondVO;
        } catch (Exception e) {
            RestfulLikeRespondVO respondVO = RestfulLikeRespondVO.error(e.getMessage());
            respondVO.bindVO(requestVO);
            return respondVO;
        }

    }

    /**
     * 执行restful请求
     * 如果未登录，会尝试登录后，再次发送一次请求
     *
     * @param host     网关服务位置，默认http://localhost:9000
     * @param resource 资源
     * @param method   方法
     * @param json     报文
     * @return 返回内容
     */
    private Map executeRestful(String host, String resource, String method, String json) throws IOException {
        // 发送restful请求
        String respond = HttpClientUtils.executeRestful(host + resource, method, json).body();
        Map body = JsonUtils.buildObject(respond, Map.class);

        // 检查：是否因为登录问题被拒绝
        if (this.isNotLogin(body)) {
            // 登录
            String url = host + "/user/login?username=" + this.constant.getLoginUserName() + "&password=" + this.constant.getLoginPassword();
            HttpClientUtils.executeRestful(url, "get");

            // 再次发送请求
            respond = HttpClientUtils.executeRestful(host + resource, method, json).body();
            body = JsonUtils.buildObject(respond, Map.class);
        }

        return body;
    }

    /**
     * 检查返回的内容是否为登录失败的响应
     * 特征是出错码=500，data=null，msg包含token字符串特征
     *
     * @param body
     * @return
     */
    private boolean isNotLogin(Map body) {
        if (!Integer.valueOf(HttpStatus.NOT_LOGIN).equals(body.get("code"))) {
            return false;
        }
        if (body.get("data") == null) {
            return false;
        }
        String msg = (String) body.get("msg");
        return msg != null;
    }
}
