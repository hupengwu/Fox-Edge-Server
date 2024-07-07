package cn.foxtech.channel.coap.client.service;

import cn.foxtech.channel.coap.client.vo.DataVO;
import cn.foxtech.channel.common.api.ChannelServerAPI;
import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.core.exception.ServiceException;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class ChannelService extends ChannelServerAPI {
    /**
     * 出错码信息
     */
    private static final String ERROR_OPERATE_FAILED = "操作失败,设备返回的错误代码为：";

    /**
     * Coap客户端
     */
    private final CoapClient client = new CoapClient();

    /**
     * 执行主从半双工操作
     *
     * @param requestVO 请求报文
     * @return 返回的json报文
     * @throws ServiceException 异常信息
     */
    @Override
    public ChannelRespondVO execute(ChannelRequestVO requestVO) throws ServiceException {
        try {
            // 转换对象
            DataVO dataVO = DataVO.buildVO(requestVO.getSend());

            // 提取数据
            int timeout = requestVO.getTimeout();
            String req = dataVO.getMethod();
            String url = dataVO.getIp() + ":" + dataVO.getPort() + "/" + dataVO.getResource();
            List<Map<String, Object>> params = dataVO.getParam();

            // 设置超时间隔
            this.client.setTimeout((long) timeout);
            // 设置生命周期
            EndpointManager.getEndpointManager().getDefaultEndpoint(CoAP.COAP_URI_SCHEME).getConfig().set(CoapConfig.EXCHANGE_LIFETIME, timeout, TimeUnit.MILLISECONDS);

            // 发出请求
            if ("get".equalsIgnoreCase(req)) {
                Object recv = this.executeGet(url, params);

                ChannelRespondVO respondVO = new ChannelRespondVO();
                respondVO.bindBaseVO(requestVO);
                respondVO.setRecv(recv);
                return respondVO;
            } else if ("post".equalsIgnoreCase(req)) {
                Object recv = this.executePost(url, params);

                ChannelRespondVO respondVO = new ChannelRespondVO();
                respondVO.bindBaseVO(requestVO);
                respondVO.setRecv(recv);
                return respondVO;
            } else if ("put".equalsIgnoreCase(req)) {
                Object recv = this.executePut(url, params);

                ChannelRespondVO respondVO = new ChannelRespondVO();
                respondVO.bindBaseVO(requestVO);
                respondVO.setRecv(recv);
                return respondVO;
            } else if ("delete".equalsIgnoreCase(req)) {
                Object recv = this.executeDelete(url, params);

                ChannelRespondVO respondVO = new ChannelRespondVO();
                respondVO.bindBaseVO(requestVO);
                respondVO.setRecv(recv);
                return respondVO;
            } else {
                throw new ServiceException("name格式不正确：" + requestVO.getName());
            }
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    private Object executeGet(String url, List<Map<String, Object>> params) throws ConnectorException, IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(url);

        // 构造带路径参数的url
        if (!params.isEmpty()) {
            sb.append("?");
        }
        for (Map<String, Object> param : params) {
            String name = (String) param.get("name");
            Object value = param.get("value");

            sb.append(name);
            sb.append("=");
            sb.append(value);
            sb.append("&");
        }
        url = sb.toString();

        url = url.substring(0, url.length() - 1);
        this.client.setURI(url);

        // 发出请求
        CoapResponse response = this.client.get();
        if (!response.isSuccess()) {
            throw new ServiceException(ERROR_OPERATE_FAILED + response.getCode());
        }

        String json = new String(response.getPayload());
        return JsonUtils.buildObject(json, List.class);
    }

    private Object executeDelete(String url, List<Map<String, Object>> params) throws ConnectorException, IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(url);

        // 构造带路径参数的url
        if (!params.isEmpty()) {
            sb.append("?");
        }
        for (Map<String, Object> param : params) {
            String name = (String) param.get("name");
            Object value = param.get("value");

            sb.append(name);
            sb.append("=");
            sb.append(value);
            sb.append("&");
        }
        url = sb.toString();

        url = url.substring(0, url.length() - 1);
        this.client.setURI(url);

        // 发出请求
        CoapResponse response = this.client.delete();
        if (!response.isSuccess()) {
            throw new ServiceException(ERROR_OPERATE_FAILED + response.getCode());
        }

        String json = new String(response.getPayload());
        return JsonUtils.buildObject(json, List.class);
    }

    private Object executePost(String url, List<Map<String, Object>> params) throws ConnectorException, IOException {
        // 填写URL
        this.client.setURI(url);

        // 构造body
        String json = JsonUtils.buildJson(params);

        // 发送数据
        CoapResponse response = this.client.post(json.getBytes(), MediaTypeRegistry.TEXT_PLAIN);
        if (!response.isSuccess()) {
            throw new ServiceException(ERROR_OPERATE_FAILED + response.getCode());
        }

        // 转换对象
        json = new String(response.getPayload());
        return JsonUtils.buildObject(json, List.class);
    }

    private Object executePut(String url, List<Map<String, Object>> params) throws ConnectorException, IOException {
        // 填写URL
        this.client.setURI(url);

        // 构造body
        String json = JsonUtils.buildJson(params);

        // 发送数据
        CoapResponse response = this.client.put(json.getBytes(), MediaTypeRegistry.TEXT_PLAIN);
        if (!response.isSuccess()) {
            throw new ServiceException(ERROR_OPERATE_FAILED + response.getCode());
        }

        // 转换对象
        json = new String(response.getPayload());
        return JsonUtils.buildObject(json, List.class);
    }
}
