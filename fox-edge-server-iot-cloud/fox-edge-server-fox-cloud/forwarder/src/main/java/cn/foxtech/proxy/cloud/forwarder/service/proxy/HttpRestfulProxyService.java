package cn.foxtech.proxy.cloud.forwarder.service.proxy;

import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.proxy.cloud.common.service.proxy.LocalHttpProxyService;
import cn.foxtech.proxy.cloud.common.vo.RestfulLikeRequestVO;
import cn.foxtech.proxy.cloud.common.vo.RestfulLikeRespondVO;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private LocalHttpProxyService localHttpProxyService;


    public void Initialize() {
        // manager-system服务
        this.proxyMapping.put("/kernel/manager", "http://localhost:9000");
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
            Map body = this.localHttpProxyService.executeRestful(requestVO.getResource(), method, json);

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
}
