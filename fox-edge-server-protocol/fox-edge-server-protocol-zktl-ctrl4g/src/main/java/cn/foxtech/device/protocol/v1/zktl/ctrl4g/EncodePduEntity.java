package cn.foxtech.device.protocol.v1.zktl.ctrl4g;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgePublish;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;
import cn.foxtech.device.protocol.v1.zktl.ctrl4g.encoder.Encoder;
import cn.foxtech.device.protocol.v1.zktl.ctrl4g.entity.ZktlConfigEntity;

import java.util.Map;

/**
 * 读取Registers
 */
@FoxEdgeDeviceType(value = "4G控制器", manufacturer = "武汉中科图灵科技有限公司")
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
     * 获取状态
     *
     * @param param 输入参数
     * @return 操作是否成功
     */
    @FoxEdgePublish
    @FoxEdgeOperate(name = "获取状态", polling = true, type = FoxEdgeOperate.encoder)
    public static String encodeGetstatus(Map<String, Object> param) {
        ZktlConfigEntity entity = new ZktlConfigEntity();
        entity.setCmd("getstatus");
        entity.setValue("");

        String pdu = Encoder.encodePdu(entity);

        return HexUtils.byteArrayToHexString(pdu.getBytes());
    }

    /**
     * 开启通道
     *
     * @param param 输入参数，范例 0x
     * @return 操作是否成功
     */
    @FoxEdgePublish
    @FoxEdgeOperate(name = "开启通道", polling = true, type = FoxEdgeOperate.encoder)
    public static String encodeSetonch(Map<String, Object> param) {
        String value = (String) param.get("value");

        // 检查：参数是否完整
        if (MethodUtils.hasEmpty(value)) {
            throw new ProtocolException("缺少配置参数：value");
        }

        ZktlConfigEntity entity = new ZktlConfigEntity();
        entity.setCmd("setonch");
        entity.setValue(value);

        String pdu = Encoder.encodePdu(entity);

        return HexUtils.byteArrayToHexString(pdu.getBytes());
    }

    /**
     * 关闭通道
     *
     * @param param 输入参数，范例 0x
     * @return 操作是否成功
     */
    @FoxEdgePublish
    @FoxEdgeOperate(name = "关闭通道", polling = true, type = FoxEdgeOperate.encoder)
    public static String encodeSetofch(Map<String, Object> param) {
        String value = (String) param.get("value");

        // 检查：参数是否完整
        if (MethodUtils.hasEmpty(value)) {
            throw new ProtocolException("缺少配置参数：value");
        }

        ZktlConfigEntity entity = new ZktlConfigEntity();
        entity.setCmd("setofch");
        entity.setValue(value);

        String pdu = Encoder.encodePdu(entity);

        return HexUtils.byteArrayToHexString(pdu.getBytes());
    }

    /**
     * 开启通道延迟后自动关闭
     *
     * @param param 输入参数，范例 0fdelay0100s
     * @return 操作是否成功
     */
    @FoxEdgePublish
    @FoxEdgeOperate(name = "开启通道延迟后自动关闭", polling = true, type = FoxEdgeOperate.encoder)
    public static String encodeSetch(Map<String, Object> param) {
        String value = (String) param.get("value");

        // 检查：参数是否完整
        if (MethodUtils.hasEmpty(value)) {
            throw new ProtocolException("缺少配置参数：value");
        }

        ZktlConfigEntity entity = new ZktlConfigEntity();
        entity.setCmd("setch");
        entity.setValue(value);

        String pdu = Encoder.encodePdu(entity);

        return HexUtils.byteArrayToHexString(pdu.getBytes());
    }

    /**
     * 配置服务器IP与端口
     *
     * @param param 输入参数，范例 122.112.245.163:8899
     * @return 操作是否成功
     */
    @FoxEdgePublish
    @FoxEdgeOperate(name = "配置服务器IP与端口", polling = true, type = FoxEdgeOperate.encoder)
    public static String encodePackTH(Map<String, Object> param) {
        String value = (String) param.get("value");

        // 检查：参数是否完整
        if (MethodUtils.hasEmpty(value)) {
            throw new ProtocolException("缺少配置参数：value");
        }

        ZktlConfigEntity entity = new ZktlConfigEntity();
        entity.setCmd("destipport=");
        entity.setValue(value + "AA");

        String pdu = Encoder.encodePdu(entity);

        return HexUtils.byteArrayToHexString(pdu.getBytes());
    }
}
