package cn.foxtech.proxy.redis.topic.service.controller;


import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.proxy.redis.topic.service.service.CoAPProxyService;
import cn.foxtech.common.utils.hex.HexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;

/**
 * 将restful请求转换为coap请求
 *
 * @author hupengwu
 */
@RestController
public class CoAPProxyController {
    @Autowired
    CoAPProxyService proxyService;
    @Value("${coap.proxy.return-format}")
    private String returnFormat = "txt";

    @Value("${coap.proxy.timeout}")
    private long timeout = 2000L;

    @RequestMapping(value = "/**")
    public AjaxResult get(HttpServletRequest request) {
        try {
            final String requestURI = request.getRequestURI();
            final String method = request.getMethod();
            final String queryString = request.getQueryString();
            BufferedReader br = request.getReader();
            String str = "";
            String body = "";
            while ((str = br.readLine()) != null) {
                body += str;
            }


            byte[] data = proxyService.execute(requestURI, queryString, body, RequestMethod.valueOf(method), timeout);
            if (returnFormat.equalsIgnoreCase("byte[]")) {
                return AjaxResult.success("ok", data);
            }
            if (returnFormat.equalsIgnoreCase("string")) {
                return AjaxResult.success("ok", new String(data));
            }
            if (returnFormat.equalsIgnoreCase("hex")) {
                return AjaxResult.success("ok", HexUtils.byteArrayToHexString(data));
            }

            return AjaxResult.success("ok", new String(data));

        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
