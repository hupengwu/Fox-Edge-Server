package cn.foxtech.link.common.api;

import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.link.domain.LinkRequestVO;
import cn.foxtech.link.domain.LinkRespondVO;

import java.util.Map;

/**
 * Link服务的API接口
 * 每一种具体的Link模块会去实现这些方法，那么Link框架在控制这些Link模块的时候，会展现出相应的特性
 */
public class LinkServerAPI {
    /**
     * 打开通道
     *
     * @param linkName  通道名称
     * @param linkParam 通道参数
     * @throws Exception 异常信息
     */
    public void openLink(String linkName, Map<String, Object> linkParam) throws Exception {
    }

    /**
     * 关闭通道
     *
     * @param linkName  通道名称
     * @param linkParam 通道参数
     */
    public void closeLink(String linkName, Map<String, Object> linkParam) {

    }

    /**
     * 管理通道操作：上层服务，对通道的管理操作
     *
     * @param requestVO 发布报文
     * @return 响应VO
     * @throws ServiceException 异常信息
     */
    public LinkRespondVO manageLink(LinkRequestVO requestVO) throws ServiceException {
        throw new ServiceException("该link不支持管理操作");
    }
}
