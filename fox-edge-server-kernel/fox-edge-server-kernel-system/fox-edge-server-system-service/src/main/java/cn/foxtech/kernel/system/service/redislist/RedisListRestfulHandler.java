package cn.foxtech.kernel.system.service.redislist;

import cn.foxtech.common.domain.vo.RestFulRequestVO;
import cn.foxtech.common.domain.vo.RestFulRespondVO;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.system.common.scheduler.RedisListRestfulScheduler;
import cn.foxtech.kernel.system.service.controller.RestfulLikeController;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 为各服务提供的Restful操作接口：该接口不进行返回，只进行静默操作
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@Component
public class RedisListRestfulHandler extends cn.foxtech.kernel.system.common.redislist.RedisListRestfulHandler {
    private static final Logger logger = Logger.getLogger(RedisListRestfulScheduler.class);

    @Autowired
    private RedisConsoleService console;


    @Autowired
    private RestfulLikeController controller;

    @Override
    public void onMessage(Object message) {
        try {
            RestFulRequestVO requestVO = JsonUtils.buildObject(message, RestFulRequestVO.class);
            if (MethodUtils.hasEmpty(requestVO.getUri(), requestVO.getMethod())) {
                throw new ServiceException("参数缺失：uri, method");
            }

            // 执行请求
            this.execute(requestVO);
        } catch (Exception e) {
            String txt = "更新设备数据，发生异常：" + e.getMessage();
            logger.error(txt);
            this.console.error(txt);
        }
    }

    public RestFulRespondVO execute(RestFulRequestVO requestVO) {
        try {
            String resource = this.controller.getResource(requestVO.getUri());
            String methodName = requestVO.getMethod().toUpperCase();

            String methodKey = resource + ":" + methodName;
            Object bean = this.controller.getBean(methodKey);
            Object method = this.controller.getMethod(methodKey);
            if (method == null || bean == null) {
                throw new ServiceException("尚未支持的方法");
            }

            // 执行controller的bean函数
            Object value = null;
            if (methodName.equals("POST") || methodName.equals("PUT")) {
                value = ((Method) method).invoke(bean, requestVO.getData());
            } else if (methodName.equals("GET") || methodName.equals("DELETE")) {
                List<Object> params = this.controller.getParams(requestVO.getUri(), (Method) method);
                if (params.size() == 0) {
                    value = ((Method) method).invoke(bean, params);
                } else if (params.size() == 1) {
                    value = ((Method) method).invoke(bean, params.get(0));
                } else if (params.size() == 2) {
                    value = ((Method) method).invoke(bean, params.get(0), params.get(1));
                } else if (params.size() == 3) {
                    value = ((Method) method).invoke(bean, params.get(0), params.get(1), params.get(2));
                } else {
                    throw new ServiceException("尚未支持的方法");
                }
            } else {
                throw new ServiceException("尚未支持的方法");
            }


            RestFulRespondVO respondVO = new RestFulRespondVO();
            respondVO.bindResVO(requestVO);
            respondVO.setData(value);

            return respondVO;
        } catch (Exception e) {
            RestFulRespondVO respondVO = RestFulRespondVO.error(e.getMessage());
            if (requestVO != null) {
                respondVO.bindResVO(requestVO);
            }

            return respondVO;
        }
    }
}