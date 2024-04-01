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
            String txt = "执行redis消息的restful请求，发生异常：" + e.getMessage();
            logger.error(txt);
            this.console.error(txt);
        }
    }

    public RestFulRespondVO execute(RestFulRequestVO requestVO) {
        try {
            Object value = this.controller.execute(requestVO.getUri(), requestVO.getMethod(), requestVO.getData());

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