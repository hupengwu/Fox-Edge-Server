package cn.foxtech.rpc.sdk.demo.test;

import cn.foxtech.channel.domain.ChannelBaseVO;
import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.domain.vo.RestFulRequestVO;
import cn.foxtech.common.domain.vo.RestFulRespondVO;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.device.domain.constant.DeviceMethodVOFieldConstant;
import cn.foxtech.device.domain.vo.OperateRequestVO;
import cn.foxtech.device.domain.vo.OperateRespondVO;
import cn.foxtech.device.domain.vo.TaskRequestVO;
import cn.foxtech.device.domain.vo.TaskRespondVO;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.constants.FoxEdgeConstant;
import cn.foxtech.rpc.sdk.redis.RpcSdkRedisClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RpcRedisSdkTester {
    private static final Logger logger = Logger.getLogger(RpcRedisSdkTester.class);

    @Autowired
    private RpcSdkRedisClient redisClient;

    public void test() {
        this.testChannel();
        this.testDevice();
        this.testPersistValueRequest();
        this.testPersistRecordRequest();
        this.testManager();
    }

    private void testChannel() {
        ChannelRequestVO channelRequestVO = new ChannelRequestVO();
        channelRequestVO.setType("simulator");
        channelRequestVO.setName("channel-simulator");
        channelRequestVO.setMode(ChannelBaseVO.MODE_EXCHANGE);
        channelRequestVO.setUuid(UUID.randomUUID().toString());
        channelRequestVO.setSend("01 03 00 00 00 01 84 0A");
        channelRequestVO.setTimeout(2000);


        this.redisClient.getChannelClient().pushChannelRequest(channelRequestVO.getType(), channelRequestVO);
        logger.info("通道服务-发送请求：" + JsonUtils.buildJsonWithoutException(channelRequestVO));

        ChannelRespondVO channelRespondVO = this.redisClient.getChannelClient().getChannelRespond(channelRequestVO.getType(), channelRequestVO.getUuid(), 2000);
        logger.info("通道服务-返回响应：" + JsonUtils.buildJsonWithoutException(channelRespondVO));
    }

    private void testDevice() {
        // 根据模板的参数，开始构造发送给设备的批量服务请求
        TaskRequestVO taskRequestVO = new TaskRequestVO();
        taskRequestVO.setClientName("my name is tester");
        taskRequestVO.setUuid(UUID.randomUUID().toString());

        Map<String, Object> deviceParam = new HashMap<>();
        Map<String, Object> operateParam = new HashMap<>();

        // 操作命令1
        OperateRequestVO operateRequestVO1 = new OperateRequestVO();
        operateRequestVO1.setManufacturer("深圳安圣电气有限公司");// 设备厂商
        operateRequestVO1.setDeviceType("CE+T UPS");// 设备类型
        operateRequestVO1.setDeviceName("CE+T UPS设备-718");//设备名称
        operateRequestVO1.setUuid(UUID.randomUUID().toString().replace("-", ""));// UUID
        operateRequestVO1.setOperateMode(DeviceMethodVOFieldConstant.value_operate_exchange);// 操作方式
        operateRequestVO1.setOperateName("Read System Measures Table");// 操作方法
        operateRequestVO1.getParam().putAll(operateParam);// 操作参数
        operateRequestVO1.getParam().putAll(deviceParam);// 设备参数
        operateRequestVO1.setTimeout(2000);// 通讯超时

        OperateRequestVO operateRequestVO2 = new OperateRequestVO();
        operateRequestVO2.setManufacturer("深圳安圣电气有限公司");// 设备厂商
        operateRequestVO2.setDeviceType("CE+T UPS");// 设备类型
        operateRequestVO2.setDeviceName("CE+T UPS设备-718");//设备名称
        operateRequestVO2.setUuid(UUID.randomUUID().toString().replace("-", ""));// UUID
        operateRequestVO2.setOperateMode(DeviceMethodVOFieldConstant.value_operate_exchange);// 操作方式
        operateRequestVO2.setOperateName("Read Alarms And Events Table");// 操作方法
        operateRequestVO2.getParam().putAll(operateParam);// 操作参数
        operateRequestVO2.getParam().putAll(deviceParam);// 设备参数
        operateRequestVO2.setTimeout(2000);// 通讯超时

        taskRequestVO.getRequestVOS().add(operateRequestVO1);
        taskRequestVO.getRequestVOS().add(operateRequestVO2);


        this.redisClient.getDeviceClient().pushDeviceRequest(taskRequestVO);
        logger.info("设备服务-发送请求：" + JsonUtils.buildJsonWithoutException(taskRequestVO));

        TaskRespondVO taskRespondVO = this.redisClient.getDeviceClient().getDeviceRespond(taskRequestVO.getUuid(), 2000);
        logger.info("设备服务-返回响应：" + JsonUtils.buildJsonWithoutException(taskRespondVO));
    }


    private void testPersistValueRequest() {
        // 发送前，可以测试一下是否拥塞程度达到了50%
        boolean isBusy = this.redisClient.getPersistClient().isValueRequestBusy(50);
        logger.info("持久化服务-达到繁忙50%：" + isBusy);
        if (isBusy) {
            logger.info("持久化服务-达到繁忙50%：" + "等待一下");
            try {
                Thread.sleep(50);
            } catch (Exception e) {
                e.getMessage();
            }
        }

        // 发送前，可以测试一下是否完全拥塞
        boolean isBlock = this.redisClient.getPersistClient().isValueRequestBlock();
        logger.info("持久化服务-是否拥塞：" + isBlock);
        if (isBlock) {
            logger.info("持久化服务-完全拥塞了：" + "等待1000毫秒");
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.getMessage();
            }
        }

        // 上面的两个测试，可以保证避免被丢弃


        // 返回结果
        OperateRespondVO operateRespondVO = new OperateRespondVO();
        operateRespondVO.setManufacturer("深圳安圣电气有限公司");// 设备厂商
        operateRespondVO.setDeviceType("CE+T UPS");// 设备类型
        operateRespondVO.setDeviceName("CE+T UPS设备-718");//设备名称

        // 操作状态
        Map<String, Object> status = new HashMap<>();
        status.put(OperateRespondVO.data_comm_status_success_time, System.currentTimeMillis());
        status.put(OperateRespondVO.data_comm_status_failed_time, 0);
        status.put(OperateRespondVO.data_comm_status_failed_count, 0);
        operateRespondVO.getData().put(OperateRespondVO.data_comm_status, status);


        // 设备Value
        Map<String, Object> value = new HashMap<>();

        // 里面携带了一个状态类型的数据
        Map<String, Object> statusValue = new HashMap<>();
        statusValue.put("逆变器06输出功率", 11);
        statusValue.put("逆变器07输出功率", 12);
        statusValue.put("逆变器08输出功率", 13);
        value.put(FoxEdgeOperate.status, statusValue);

        operateRespondVO.getData().put(OperateRespondVO.data_value, value);

        // 将操作结果，打包成成TaskRespondVO
        TaskRespondVO respondVO = new TaskRespondVO();
        respondVO.getRespondVOS().add(operateRespondVO);

        // 将Value类数据，推送到Value的Redis队列
        this.redisClient.getPersistClient().pushValueRequest(respondVO);
        logger.info("持久化服务-发送请求：" + JsonUtils.buildJsonWithoutException(respondVO));
    }

    private void testPersistRecordRequest() {
        // 返回结果
        OperateRespondVO operateRespondVO = new OperateRespondVO();
        operateRespondVO.setManufacturer("深圳安圣电气有限公司");// 设备厂商
        operateRespondVO.setDeviceType("CE+T UPS");// 设备类型
        operateRespondVO.setDeviceName("CE+T UPS设备-718");//设备名称

        // 操作状态
        Map<String, Object> status = new HashMap<>();
        status.put(OperateRespondVO.data_comm_status_success_time, System.currentTimeMillis());
        status.put(OperateRespondVO.data_comm_status_failed_time, 0);
        status.put(OperateRespondVO.data_comm_status_failed_count, 0);
        operateRespondVO.getData().put(OperateRespondVO.data_comm_status, status);


        // 设备Value
        Map<String, Object> value = new HashMap<>();

        // 里面携带了一个状态类型的数据
        List<Map<String, Object>> records = new ArrayList<>();// 可以携带多条record
        // 第一条记录
        Map<String, Object> record1 = new HashMap<>();// 第一条record
        record1.put(FoxEdgeConstant.RECORD_TYPE_TAG, "门禁记录");//必填字段：记录的类型
        record1.put("动作", "刷卡进门");// 可填字段：xxx
        record1.put("时间", System.currentTimeMillis());// 可填字段：xxx
        record1.put("人员", "测试人员");// 可填字段：xxx
        records.add(record1);// 放到数组中
        value.put(FoxEdgeOperate.record, records);// 把记录列表，放到record类型中

        operateRespondVO.getData().put(OperateRespondVO.data_value, value);// 告知：操作结果中有内容

        // 将操作结果，打包成成TaskRespondVO
        TaskRespondVO respondVO = new TaskRespondVO();
        respondVO.getRespondVOS().add(operateRespondVO);

        // 将Record类数据，推送到Record的Redis队列
        this.redisClient.getPersistClient().pushRecordRequest(respondVO);
        logger.info("持久化服务-发送请求：" + JsonUtils.buildJsonWithoutException(respondVO));
    }

    private void testManager() {
        Map<String, Object> body = new HashMap<>();
        body.put("pageNum", 1);
        body.put("pageSize", 10);

        RestFulRequestVO requestVO = new RestFulRequestVO();
        requestVO.setUuid(UUID.randomUUID().toString());
        requestVO.setUri("/device/record/page");
        requestVO.setMethod("post");
        requestVO.setData(body);

        // 发出请求
        this.redisClient.getManagerClient().pushRequest(requestVO);
        logger.info("管理服务-发送请求：" + JsonUtils.buildJsonWithoutException(requestVO));

        // 获得
        RestFulRespondVO restFulRespondVO = this.redisClient.getManagerClient().queryRespond(requestVO.getUuid(), 2000);
        logger.info("管理服务-返回响应：" + JsonUtils.buildJsonWithoutException(restFulRespondVO));

        // 发出请求
        String uuid = UUID.randomUUID().toString();
        this.redisClient.getManagerClient().pushRequest(uuid, "/device/page", "post", body);
        logger.info("管理服务-发送请求：" + JsonUtils.buildJsonWithoutException(body));

        // 返回请求
        restFulRespondVO = this.redisClient.getManagerClient().queryRespond(uuid, 2000);
        logger.info("管理服务-返回响应：" + JsonUtils.buildJsonWithoutException(restFulRespondVO));
    }
}
