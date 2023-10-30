package cn.foxtech.proxy.redis.topic.service.service;

import cn.foxtech.core.exception.ServiceException;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.core.network.Endpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class CoAPProxyService {
    private final CoapClient client = new CoapClient();

    public byte[] execute(String url, String queryString, String body, RequestMethod method, long timeout) throws ConnectorException, IOException {
        // 设置URI
        String coapURI = this.buildCoAPUri(url, queryString);
        this.client.setURI(coapURI);

        // 设置通信超时
        this.client.setTimeout(timeout);

        // 设置生命周期
        Endpoint endpoint = EndpointManager.getEndpointManager().getDefaultEndpoint(CoAP.COAP_URI_SCHEME);
        Configuration config = endpoint.getConfig();
        config.set(CoapConfig.EXCHANGE_LIFETIME, timeout, TimeUnit.MILLISECONDS);

        // 发出请求
        CoapResponse response = null;
        if (RequestMethod.GET.equals(method)) {
            response = client.get();
        }
        if (RequestMethod.POST.equals(method)) {
            response = client.post(body, MediaTypeRegistry.TEXT_PLAIN);
        }
        if (RequestMethod.PUT.equals(method)) {
            response = client.put(body, MediaTypeRegistry.TEXT_PLAIN);
        }
        if (RequestMethod.DELETE.equals(method)) {
            response = client.delete();
        }

        // 异常处理
        if (response == null) {
            throw new ServiceException("设备访问超时：" + coapURI);
        }
        if (!response.getCode().isSuccess()) {
            throw new ServiceException("设备访问失败：" + response.getCode());
        }

        // 返回结果
        return response.getPayload();
    }

    /**
     * 分拆URI
     * 格式必须为这类格式：http://127.0.0.1:9003/coap://192.168.1.3:5683/time/my
     * 分拆的结果是：192.168.1.3:5683/time/my
     *
     * @param uri uri
     * @return 192.168.1.3:5683/time/my
     */
    private String buildCoAPUri(String uri, String queryString) {
        String[] path = uri.split("/");
        if (path.length < 4) {
            throw new ServiceException("uri不正确：" + uri);
        }
        String coapHead = path[1];
        if (!"coap:".equals(coapHead)) {
            throw new ServiceException("uri不正确，格式必须为http://xx.xx.xx.xx:9003/coap://yy.yy.yy.yy:zz/url：" + uri);
        }
        String coapHost = path[3];
        if (coapHost.split(":").length != 2) {
            throw new ServiceException("uri不正确，必须包含coap前缀：" + uri);
        }
        String coapURI = uri.substring("/".length() + coapHead.length() + "//".length() + coapHost.length());
        return coapHost + coapURI + "?" + queryString;
    }
}
