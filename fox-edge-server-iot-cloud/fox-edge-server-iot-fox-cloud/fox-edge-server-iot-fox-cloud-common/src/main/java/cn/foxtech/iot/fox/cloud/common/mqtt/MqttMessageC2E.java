package cn.foxtech.iot.fox.cloud.common.mqtt;

import cn.foxtech.iot.fox.cloud.common.vo.RestfulLikeRequestVO;
import cn.foxtech.iot.fox.cloud.common.vo.RestfulLikeRespondVO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fox-Cloud对Fox-Edge发起的请求
 * 比如，Fox-Cloud想通过MQTT对Fox-Edge进行远程控制操作
 */
@Component
public class MqttMessageC2E {
    public static final String TYPE_REQUEST = "request";
    public static final String TYPE_RESPOND = "respond";

    /**
     * request的发送队列
     */
    private final Map<String, RestfulLikeRequestVO> requestList = new HashMap<>();
    /**
     * respond的发送队列
     */
    private final List<RestfulLikeRespondVO> respondVOList = new ArrayList<>();

    public synchronized void insertRespondVO(RestfulLikeRespondVO respondVO) {
        this.respondVOList.add(respondVO);
    }

    public synchronized List<RestfulLikeRespondVO> removeRespondVOList() {
        List<RestfulLikeRespondVO> requestVOList = new ArrayList<>();
        requestVOList.addAll(this.respondVOList);
        this.respondVOList.clear();
        return requestVOList;
    }

    private boolean isEmptyRespond() {
        return this.respondVOList.isEmpty();
    }

    public synchronized void insertRequestVO(RestfulLikeRequestVO requestVO) {
        requestVO.setExecuteTime(0);
        this.requestList.put(requestVO.getUuid(), requestVO);
    }

    public synchronized boolean isEmpty() {
        return (this.isEmptyRequest() && this.isEmptyRespond());
    }

    public synchronized boolean isEmpty(String type) {
        if (MqttMessageC2E.TYPE_REQUEST.equals(type)) {
            return this.isEmptyRequest();
        }
        if (MqttMessageC2E.TYPE_RESPOND.equals(type)) {
            return this.isEmptyRespond();
        }


        return (this.isEmptyRequest() && this.isEmptyRespond());
    }


    private boolean isEmptyRequest() {
        // 检查：是否有数据到达
        if (this.requestList.isEmpty()) {
            return true;
        }

        // 检查：是否有尚未处理的数据
        for (Map.Entry<String, RestfulLikeRequestVO> entry : this.requestList.entrySet()) {
            RestfulLikeRequestVO requestVO = entry.getValue();
            if (requestVO.getExecuteTime() == 0) {
                return false;
            }
        }

        // 此时：全是发送过的数据，处于等待响应阶段
        return true;
    }

    public synchronized List<RestfulLikeRequestVO> queryRequestVOList() {
        List<RestfulLikeRequestVO> requestVOList = new ArrayList<>();

        for (Map.Entry<String, RestfulLikeRequestVO> entry : this.requestList.entrySet()) {
            RestfulLikeRequestVO requestVO = entry.getValue();
            if (requestVO.getExecuteTime() == 0) {
                requestVOList.add(requestVO);
            }
        }

        return requestVOList;
    }

    public synchronized void updateRequestVO(String uuid, long time) {
        RestfulLikeRequestVO requestVO = this.requestList.get(uuid);
        if (requestVO == null) {
            return;
        }

        requestVO.setExecuteTime(time);
    }

    /**
     * 删除处理完成的任务
     *
     * @param uuid uuid
     */
    public synchronized void deleteRequestVO(String uuid) {
        this.requestList.remove(uuid);
    }

    public synchronized void deleteRequestVO(long timeout) {
        List<String> uuidList = new ArrayList<>();
        long time = System.currentTimeMillis() + timeout;

        // 查找过期没响应的数据
        for (Map.Entry<String, RestfulLikeRequestVO> entry : this.requestList.entrySet()) {
            RestfulLikeRequestVO requestVO = entry.getValue();
            if (requestVO.getExecuteTime() > time) {
                uuidList.add(requestVO.getUuid());
            }
        }

        // 删除数据
        for (String uuid : uuidList) {
            this.requestList.remove(uuid);
        }
    }
}
