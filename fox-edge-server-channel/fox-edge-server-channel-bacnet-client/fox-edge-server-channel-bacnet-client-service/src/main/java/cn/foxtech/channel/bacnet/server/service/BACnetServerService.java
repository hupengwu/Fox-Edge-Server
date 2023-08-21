package cn.foxtech.channel.bacnet.server.service;

import cn.foxtech.channel.bacnet.server.entity.BACnetDataEntity;
import cn.foxtech.channel.bacnet.server.util.BACnetUtils;
import cn.foxtech.channel.common.api.ChannelServerAPI;
import cn.foxtech.channel.bacnet.domain.vo.DataVO;
import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.core.exception.ServiceException;
import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.exception.BACnetErrorException;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.Primitive;
import com.serotonin.bacnet4j.util.RequestUtils;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BACnetServerService extends ChannelServerAPI {
    @Getter
    private final BACnetDataEntity dataEntity = new BACnetDataEntity();

    /**
     * 从配置文件中读取配置
     */
    public void loadConfig() {
        try {
            // 读取JSON格式的配置文件
            File file = new File("");
            String confFileName = file.getAbsolutePath() + "/conf/fox-edge-server-channe-bacnet.conf";

            String jsonData = new String(Files.readAllBytes(Paths.get(confFileName)));

            // 构造数据
            this.dataEntity.build(jsonData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建本地虚拟设备
     */
    public void createLocalDevice() {
        try {
            // 根据配置信息创建本地虚拟设备
            LocalDevice localDevice = BACnetUtils.initLocalDevice(this.dataEntity.getLocalDeviceIp(), this.dataEntity.getLocalDevicePort(), this.dataEntity.getLocalDeviceId());
            this.dataEntity.setLocalDevice(localDevice);
        } catch (Exception e) {
            e.printStackTrace();

            if (this.dataEntity.getLocalDevice() != null) {
                this.dataEntity.getLocalDevice().terminate();
                this.dataEntity.setLocalDevice(null);
            }

        }
    }

    /**
     * 发现远端设备
     */
    public void discoveryRemoteDevice() {
        try {
            LocalDevice localDevice = this.dataEntity.getLocalDevice();
            if (localDevice == null) {
                return;
            }

            // 对远端物理设备们进行网络异步发现，并等待一定的响应时间，使远端设备能够有足够的时间进行应答
            localDevice.startRemoteDeviceDiscovery();
            Thread.sleep(this.dataEntity.getDiscoveryTime());

        } catch (Exception e) {
            e.printStackTrace();

            if (this.dataEntity.getLocalDevice() != null) {
                this.dataEntity.getLocalDevice().terminate();
                this.dataEntity.setLocalDevice(null);
            }
        }
    }

    /**
     * 查询远端设备的数据:增加同步锁，避免并发访问带来的多线程异常。
     *
     * @param requestVO 发送请求
     * @return 响应报文
     * @throws ServiceException 异常
     */
    @Override
    public synchronized ChannelRespondVO execute(ChannelRequestVO requestVO) throws ServiceException {
        try {
            // 转换对象
            Integer remoteDeviceId = Integer.parseInt(requestVO.getName());
            DataVO dataVO = DataVO.buildVO(requestVO.getSend());


            LocalDevice localDevice = this.dataEntity.getLocalDevice();
            if (localDevice == null) {
                throw new ServiceException("本地设备为null，本地设备初始化不正确，请检查配置文件的内容，比如IP是否正确、端口");
            }

            RemoteDevice remoteDevice = BACnetUtils.getRemoteDevice(localDevice, remoteDeviceId);

            // 读取数据
            if (dataVO.getResource().equals("value") && dataVO.getMethod().equals("get")) {
                DataVO recv = this.executeGetValue(localDevice, remoteDevice, dataVO, requestVO.getTimeout());

                ChannelRespondVO respondVO = new ChannelRespondVO();
                respondVO.bindBaseVO(requestVO);
                respondVO.setRecv(recv);
                return respondVO;
            }
            // 写入数据
            if (dataVO.getResource().equals("value") && dataVO.getMethod().equals("set")) {
                DataVO recv = this.executeSetValue(localDevice, remoteDevice, dataVO, requestVO.getTimeout());

                ChannelRespondVO respondVO = new ChannelRespondVO();
                respondVO.bindBaseVO(requestVO);
                respondVO.setRecv(recv);
                return respondVO;
            }

            throw new ServiceException("不支持的操作");
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * 执行读取数据
     *
     * @param localDevice  本地设备
     * @param remoteDevice 远端设备
     * @param dataVO       数据请求内容
     * @return 数据应答内容
     * @throws Exception 操作异常
     */
    private synchronized DataVO executeGetValue(LocalDevice localDevice, RemoteDevice remoteDevice, DataVO dataVO, int timeOut) throws Exception {
        // 取出远端设备包含的全部OID对象：这是设备发现阶段获得的
        List<ObjectIdentifier> objectList = RequestUtils.getObjectList(localDevice, remoteDevice).getValues();

        // 滤掉不能读取的有毒数据
        objectList = BACnetUtils.filterCannotReadOids(objectList);

        // 筛选出需要操作的数据
        if (dataVO.getParam() != null) {
            // 读取指定数据：过滤不能读取的有毒数据，并过滤不再指定范围内的数据
            objectList = this.filterReadOids(objectList, dataVO.getParam());
        }

        // 批量读取远端设备的数据
        List<Map<String, Object>> values = BACnetUtils.readOidPresentValues(localDevice, remoteDevice, objectList, timeOut);
        dataVO.setParam(values);
        return dataVO;
    }

    /**
     * 执行设备数据
     *
     * @param localDevice  本地设备
     * @param remoteDevice 远端设备
     * @param dataVO       数据请求内容
     * @return 数据应答内容
     * @throws Exception 操作异常
     */
    private synchronized DataVO executeSetValue(LocalDevice localDevice, RemoteDevice remoteDevice, DataVO dataVO, int timeOut) throws Exception {
        // 取出远端设备包含的全部OID对象：这是设备发现阶段获得的
        List<ObjectIdentifier> objectList = RequestUtils.getObjectList(localDevice, remoteDevice).getValues();

        // 滤掉不能读取的有毒数据
        objectList = BACnetUtils.filterCannotReadOids(objectList);

        // 筛选出需要操作的数据
        if (dataVO.getParam() == null) {
            throw new ServiceException("参数表为空");
        }
        List<Map<String, Object>> valueParamList = null;
        if (dataVO.getParam() != null) {
            // 读取指定数据：过滤不能读取的有毒数据，并过滤不再指定范围内的数据
            valueParamList = this.filterWriteOids(objectList, dataVO.getParam());
        }
        if (valueParamList == null || valueParamList.isEmpty()) {
            throw new ServiceException("没有可以设置的参数");
        }

        for (Map<String, Object> valueParam : valueParamList) {
            ObjectIdentifier oid = (ObjectIdentifier) valueParam.get("oid");
            Primitive value = (Primitive) valueParam.get("value");

            // 批量读取远端设备的数据
            BACnetUtils.writePresentValue(localDevice, remoteDevice.getInstanceNumber(), oid, value, timeOut);
        }

        return dataVO;
    }

    private List<ObjectIdentifier> filterReadOids(List<ObjectIdentifier> oids, List<Map<String, Object>> params) {
        List<ObjectIdentifier> result = new ArrayList<>();
        for (ObjectIdentifier oid : oids) {

            boolean finded = false;
            for (Map<String, Object> param : params) {
                if (!isMatch(oid, param)) {
                    continue;
                }

                finded = true;
                break;
            }

            if (finded) {
                result.add(oid);
            }
        }

        return result;
    }

    private List<Map<String, Object>> filterWriteOids(List<ObjectIdentifier> oids, List<Map<String, Object>> params) throws ParseException, BACnetErrorException {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (ObjectIdentifier oid : oids) {
            for (Map<String, Object> param : params) {
                // OID是否匹配
                if (!isMatch(oid, param)) {
                    continue;
                }
                String valueType = (String) param.get("value_type");
                if (valueType == null) {
                    continue;
                }
                Object value = param.get("value");
                if (value == null) {
                    continue;
                }

                Primitive primitive = BACnetUtils.buildEncodable(valueType, value.toString());

                Map<String, Object> result = new HashMap<>();
                result.put("oid", oid);
                result.put("value", primitive);
                resultList.add(result);
            }
        }

        return resultList;
    }

    /**
     * 检查OID参数是否找到
     *
     * @param oid
     * @param param
     * @return
     */
    private boolean isMatch(ObjectIdentifier oid, Map<String, Object> param) {
        String objectType = (String) param.get("object_type");
        Integer id = (Integer) param.get("oid");
        if (objectType == null || id == null) {
            return false;
        }

        if (!oid.getObjectType().toString().equals(objectType)) {
            return false;
        }
        return id.equals(oid.getInstanceNumber());
    }


}
