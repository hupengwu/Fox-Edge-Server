package cn.foxtech.zkturingl.service.remote;

import cn.foxtech.common.constant.HttpStatus;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.osinfo.OSInfoUtils;
import cn.foxtech.common.utils.syncobject.SyncFlagObjectMap;
import cn.foxtech.common.utils.uuid.UuidUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.iot.common.remote.RemoteMqttService;
import cn.foxtech.zkturingl.service.service.ZKTuringlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class RemoteService {
    private final String edgeId = OSInfoUtils.getCPUID();
    @Autowired
    private RemoteMqttService remoteMqttService;
    @Autowired
    private ZKTuringlService turinglService;

    private Map<String, Object> executePost(String topic, Map<String, Object> body) throws InterruptedException {
        // 补齐uuid
        String uuid = (String) body.get("uuid");
        if (MethodUtils.hasEmpty(uuid)) {
            uuid = UuidUtils.randomUUID();
            body.put("uuid", uuid);
        }

        // 重置信号
        SyncFlagObjectMap.inst().reset(uuid);

        // 发出数据
        String json = JsonUtils.buildJsonWithoutException(body);
        this.remoteMqttService.getClient().publish(topic, json.getBytes());

        // 等待数据返回：
        return (Map<String, Object>) SyncFlagObjectMap.inst().waitDynamic(uuid, 60 * 1000);
    }

    public void publishEntity(String entityType, Map<String, Object> dataList) throws IOException, InterruptedException {
        String topic = this.turinglService.getPublish() + "/device/record/rows";

        Map<String, Object> body = new HashMap<>();
        body.put("edgeId", this.edgeId);
        body.put("entityType", entityType);
        body.put("data", new HashMap<>());

        if (dataList != null) {
            body.put("data", dataList);
        }


        String json = JsonUtils.buildJsonWithoutException(body);
        this.remoteMqttService.getClient().publish(topic, json.getBytes());
    }

    public Object queryTimestamp() throws ServiceException, InterruptedException {
        String topic = this.turinglService.getPublish() + "/device/record/timestamp";
        Map<String, Object> data = new HashMap<>();
        data.put("edgeId", edgeId);
        data.put("entityType", "deviceRecord");
        Map<String, Object> body = new HashMap<>();
        body.put("data", data);
        //   body.put("uuid", "4e07580e1a364b28872411178c7ec437");

        Map<String, Object> respond = this.executePost(topic, body);

        // 检查：是否返回了异常
        Integer code = (Integer) respond.get("code");
        if (!HttpStatus.SUCCESS.equals(code)) {
            throw new ServiceException("未返回成功代码，这是一个异常的返回:" + respond.get("msg"));
        }

        // 检查：是否有返回了数据
        data = (Map<String, Object>) respond.get("data");
        if (data == null) {
            throw new ServiceException("返回的data为空");
        }

        return data.get("lastId");
    }
}
