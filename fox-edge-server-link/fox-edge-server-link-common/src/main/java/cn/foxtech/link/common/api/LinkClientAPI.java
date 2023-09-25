package cn.foxtech.link.common.api;

import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.link.common.properties.LinkProperties;
import cn.foxtech.link.common.service.EntityManageService;
import cn.foxtech.link.domain.LinkRequestVO;
import cn.foxtech.link.domain.LinkRespondVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Link服务的API接口
 * 每一种具体的Link模块会去实现这些方法，那么Link框架在控制这些Link模块的时候，会展现出相应的特性
 */
@Component
public class LinkClientAPI {
    private static final Logger LOGGER = LoggerFactory.getLogger(LinkClientAPI.class);

    @Autowired
    private LinkProperties linkProperties;

    @Autowired
    private LinkServerAPI linkServerAPI;

    @Autowired
    private EntityManageService entityManageService;

    /**
     * 打开通道
     *
     * @param linkName  通道名称
     * @param linkParam 通道参数
     * @throws Exception 异常
     */
    public void openLink(String linkName, Map<String, Object> linkParam) throws Exception {
        this.linkServerAPI.openLink(linkName, linkParam);
    }

    /**
     * 关闭通道
     *
     * @param linkName  通道名称
     * @param linkParam 通道参数
     */
    public void closeLink(String linkName, Map<String, Object> linkParam) {
        this.linkServerAPI.closeLink(linkName, linkParam);
    }

    /**
     * 对通道进行管理操作
     *
     * @param requestVO 操作请求
     * @return 响应
     * @throws ServiceException 异常状况
     */
    public LinkRespondVO manageLink(LinkRequestVO requestVO) throws ServiceException {
        return this.linkServerAPI.manageLink(requestVO);
    }
}
