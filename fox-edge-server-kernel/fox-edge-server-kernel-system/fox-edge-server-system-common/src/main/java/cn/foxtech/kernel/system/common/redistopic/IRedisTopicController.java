package cn.foxtech.kernel.system.common.redistopic;

import cn.foxtech.common.domain.vo.RestFulRequestVO;
import cn.foxtech.common.domain.vo.RestFulRespondVO;

public interface IRedisTopicController {
    public RestFulRespondVO execute(RestFulRequestVO requestVO);
}
