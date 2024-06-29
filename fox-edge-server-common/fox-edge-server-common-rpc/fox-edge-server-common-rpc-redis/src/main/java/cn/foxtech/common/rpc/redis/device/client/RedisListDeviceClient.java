package cn.foxtech.common.rpc.redis.device.client;

import cn.foxtech.device.domain.vo.TaskRequestVO;
import cn.foxtech.device.domain.vo.TaskRespondVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisListDeviceClient {
    @Autowired
    private RedisListDeviceClientRespond deviceRespond;

    @Autowired
    private RedisListDeviceClientRequest deviceRequest;

    @Autowired
    private RedisListDeviceClientReport deviceReport;

    public TaskRespondVO getDeviceRespond(String uuid, long timeout) {
        return this.deviceRespond.get(uuid, timeout);
    }


    public void pushDeviceRequest(TaskRequestVO requestVO) {
        this.deviceRequest.push(requestVO);
    }

    public TaskRespondVO popDeviceReport(long timeout, TimeUnit unit) {
        return this.deviceReport.pop(timeout, unit);
    }


}
