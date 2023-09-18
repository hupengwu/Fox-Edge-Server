package cn.foxtech.manager.system.controller;


import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.manager.system.service.RedisTopicProxyService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/kernel/manager/proxy-redis-topic")
public class RedisTopicProxyController {
    @Autowired
    RedisTopicProxyService proxyService;


    @RequestMapping(value = "/**")
    public Object get(HttpServletRequest request) {
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

            return proxyService.execute(requestURI.substring("/kernel/manager/proxy-redis-topic".length()), queryString, body, RequestMethod.valueOf(method));
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
