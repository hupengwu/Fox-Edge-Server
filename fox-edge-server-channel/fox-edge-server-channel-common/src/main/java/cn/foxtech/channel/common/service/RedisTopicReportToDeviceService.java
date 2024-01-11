package cn.foxtech.channel.common.service;

import cn.foxtech.channel.common.api.ChannelClientAPI;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.domain.constant.RedisTopicConstant;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.redis.topic.service.RedisTopicPublisher;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisTopicReportToDeviceService extends PeriodTaskService {
    private final String deviceTopic = RedisTopicConstant.topic_channel_respond + RedisTopicConstant.model_device;

    @Autowired
    private RedisTopicPublisher publisher;

    @Autowired
    private ChannelClientAPI channelService;

    /**
     * 执行任务
     * 步骤：
     * 1.获取全局设备列表和操作列表，进行循环遍历操作
     * 2.从全局设备配置中，获得设备的配置参数
     * 3.将操作请求编码后发送给设备，并对设备返回的数据进行解码，得到设备的各项数值
     * 4.将设备的各项数值，写入redis保存，通过redis分享给其他服务
     * <p>
     * 说明：SyncQueueObjectMap.inst().popup默认waite了100毫秒，所以不再需要专门的sleep(100)
     *
     * @throws Exception 异常情况
     */
    public void execute(long threadId) throws Exception {
        List<ChannelRespondVO> respondVOList = this.channelService.reportTo(1 * 1000);
        for (ChannelRespondVO respondVO : respondVOList) {
            // 标识为主动上报模式
            respondVO.setMode(ChannelRespondVO.MODE_RECEIVE);

            String json = JsonUtils.buildJsonWithoutException(respondVO);
            this.publisher.sendMessage(this.deviceTopic, json);
        }
    }
}
