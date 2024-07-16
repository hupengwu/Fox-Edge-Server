package cn.foxtech.channel.http.client.service;


import cn.foxtech.channel.common.api.ChannelServerAPI;
import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.domain.vo.RestFulRequestVO;
import cn.foxtech.common.utils.http.HttpClientUtil;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.exception.ServiceException;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

@Component
public class HttpClientService extends ChannelServerAPI {

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
            RestFulRequestVO restfulVO = RestFulRequestVO.buildVO((Map) requestVO.getSend());

            // 检查：用户是否填写了参数
            if (MethodUtils.hasEmpty(restfulVO.getUri(), restfulVO.getMethod())) {
                throw new ServiceException("参数不能为空：uri, method");
            }

            if (restfulVO.getMethod().equalsIgnoreCase("get")) {
                String uri = this.buildUri(restfulVO.getUri(), (Map) restfulVO.getData());
                Map map = HttpClientUtil.executeGet(uri, Map.class);

                ChannelRespondVO respondVO = new ChannelRespondVO();
                respondVO.bindBaseVO(requestVO);
                respondVO.setRecv(map);
                return respondVO;
            }
            if (restfulVO.getMethod().equalsIgnoreCase("post")) {
                Map map = HttpClientUtil.executePost(restfulVO.getUri(), restfulVO.getData(), Map.class);

                ChannelRespondVO respondVO = new ChannelRespondVO();
                respondVO.bindBaseVO(requestVO);
                respondVO.setRecv(map);
                return respondVO;
            }
            if (restfulVO.getMethod().equalsIgnoreCase("put")) {
                Map map = HttpClientUtil.executePut(restfulVO.getUri(), restfulVO.getData(), Map.class);

                ChannelRespondVO respondVO = new ChannelRespondVO();
                respondVO.bindBaseVO(requestVO);
                respondVO.setRecv(map);
                return respondVO;
            }


            throw new ServiceException("不支持的操作！");
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    private String buildUri(String uri, Map<String, Object> params) throws UnsupportedEncodingException {
        // 场景1：没有夹带param参数，说明用户传递的是简单uri
        if (params == null || params.isEmpty()) {
            return uri;
        }

        // 场景2：用户夹带了param参数，没有填写格式，那么说明按restful规范填写参数
        if (uri.indexOf("?") < 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(uri);
            sb.append("?");

            int size = params.size();
            for (String key : params.keySet()) {
                // 转base64
                sb.append(URLEncoder.encode(key, "UTF-8"));

                sb.append("=");

                // 转base64
                Object par = params.getOrDefault(key, "");
                if (par instanceof String) {
                    par = URLEncoder.encode((String) par, "UTF-8");
                }
                sb.append(par);

                // 追加&
                if (1 < size--) {
                    sb.append("&");
                }
            }

            return sb.toString();
        }

        return uri;
    }
}
