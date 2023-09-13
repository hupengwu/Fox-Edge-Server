package cn.foxtech.device.protocol.v1.zktl.electric;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgePublish;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;
import cn.foxtech.device.protocol.v1.zktl.electric.encoder.Encoder;
import cn.foxtech.device.protocol.v1.zktl.electric.entity.ZktlConfigEntity;

import java.util.Map;

/**
 * 读取Registers
 */
@FoxEdgeDeviceType(value = "电器火灾监控设备", manufacturer = "武汉中科图灵科技有限公司")
public class EncodePduEntity {
    /**
     * 查询传感器状态
     *
     * @param param 输入参数
     * @return 操作是否成功
     */
    @FoxEdgePublish
    @FoxEdgeOperate(name = "设置设备参数", polling = true, type = FoxEdgeOperate.encoder)
    public static String encodePackCmdValue(Map<String, Object> param) {
        String cmd = (String) param.get("cmd");
        String value = (String) param.get("value");

        // 检查：参数是否完整
        if (MethodUtils.hasEmpty(cmd, value)) {
            throw new ProtocolException("缺少配置参数：cmd, value");
        }

        ZktlConfigEntity entity = new ZktlConfigEntity();
        entity.setCmd(cmd);
        entity.setValue(value);

        String pdu = Encoder.encodePdu(entity);

        return HexUtils.byteArrayToHexString(pdu.getBytes());
    }

    /**
     * 通道配置
     *
     * @param param 输入参数，范例 015
     * @return 操作是否成功
     */
    @FoxEdgePublish
    @FoxEdgeOperate(name = "通道配置", polling = true, type = FoxEdgeOperate.encoder)
    public static String encodePackCH(Map<String, Object> param) {
        String value = (String) param.get("value");

        // 检查：参数是否完整
        if (MethodUtils.hasEmpty(value)) {
            throw new ProtocolException("缺少配置参数：value");
        }

        ZktlConfigEntity entity = new ZktlConfigEntity();
        entity.setCmd("CH");
        entity.setValue(value);

        String pdu = Encoder.encodePdu(entity);

        return HexUtils.byteArrayToHexString(pdu.getBytes());
    }

    /**
     * 电压低门限配置
     *
     * @param param 输入参数，范例 200V
     * @return 操作是否成功
     */
    @FoxEdgePublish
    @FoxEdgeOperate(name = "电压低门限配置", polling = true, type = FoxEdgeOperate.encoder)
    public static String encodePackUL(Map<String, Object> param) {
        String value = (String) param.get("value");

        // 检查：参数是否完整
        if (MethodUtils.hasEmpty(value)) {
            throw new ProtocolException("缺少配置参数：value");
        }

        ZktlConfigEntity entity = new ZktlConfigEntity();
        entity.setCmd("UL");
        entity.setValue(value);

        String pdu = Encoder.encodePdu(entity);

        return HexUtils.byteArrayToHexString(pdu.getBytes());
    }

    /**
     * 电流高门限配置
     *
     * @param param 输入参数，范例 080A
     * @return 操作是否成功
     */
    @FoxEdgePublish
    @FoxEdgeOperate(name = "电流高门限配置", polling = true, type = FoxEdgeOperate.encoder)
    public static String encodePackIH(Map<String, Object> param) {
        String value = (String) param.get("value");

        // 检查：参数是否完整
        if (MethodUtils.hasEmpty(value)) {
            throw new ProtocolException("缺少配置参数：value");
        }

        ZktlConfigEntity entity = new ZktlConfigEntity();
        entity.setCmd("IH");
        entity.setValue(value);

        String pdu = Encoder.encodePdu(entity);

        return HexUtils.byteArrayToHexString(pdu.getBytes());
    }

    /**
     * 剩余电流高门限配置
     *
     * @param param 输入参数，范例 500mA
     * @return 操作是否成功
     */
    @FoxEdgePublish
    @FoxEdgeOperate(name = "剩余电流高门限配置", polling = true, type = FoxEdgeOperate.encoder)
    public static String encodePackINH(Map<String, Object> param) {
        String value = (String) param.get("value");

        // 检查：参数是否完整
        if (MethodUtils.hasEmpty(value)) {
            throw new ProtocolException("缺少配置参数：value");
        }

        ZktlConfigEntity entity = new ZktlConfigEntity();
        entity.setCmd("INH");
        entity.setValue(value);

        String pdu = Encoder.encodePdu(entity);

        return HexUtils.byteArrayToHexString(pdu.getBytes());
    }

    /**
     * 温度高门限配置
     *
     * @param param 输入参数，范例 080C
     * @return 操作是否成功
     */
    @FoxEdgePublish
    @FoxEdgeOperate(name = "温度高门限配置", polling = true, type = FoxEdgeOperate.encoder)
    public static String encodePackTH(Map<String, Object> param) {
        String value = (String) param.get("value");

        // 检查：参数是否完整
        if (MethodUtils.hasEmpty(value)) {
            throw new ProtocolException("缺少配置参数：value");
        }

        ZktlConfigEntity entity = new ZktlConfigEntity();
        entity.setCmd("TH");
        entity.setValue(value);

        String pdu = Encoder.encodePdu(entity);

        return HexUtils.byteArrayToHexString(pdu.getBytes());
    }

    /**
     * 消音配置
     *
     * @param param 输入参数，范例 080C
     * @return 操作是否成功
     */
    @FoxEdgePublish
    @FoxEdgeOperate(name = "消音配置", polling = true, type = FoxEdgeOperate.encoder)
    public static String encodePackMUTE(Map<String, Object> param) {
        String value = (String) param.get("value");

        // 检查：参数是否完整
        if (MethodUtils.hasEmpty(value)) {
            throw new ProtocolException("缺少配置参数：value");
        }

        ZktlConfigEntity entity = new ZktlConfigEntity();
        entity.setCmd("MUTE");
        entity.setValue(value);

        String pdu = Encoder.encodePdu(entity);

        return HexUtils.byteArrayToHexString(pdu.getBytes());
    }
}
