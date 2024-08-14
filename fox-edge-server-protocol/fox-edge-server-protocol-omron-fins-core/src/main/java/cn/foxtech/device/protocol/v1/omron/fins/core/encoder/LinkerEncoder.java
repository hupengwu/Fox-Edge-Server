/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.omron.fins.core.encoder;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeLinkerAction;
import cn.foxtech.device.protocol.v1.omron.fins.core.entity.data.Respond;
import cn.foxtech.device.protocol.v1.omron.fins.core.entity.pdu.ConnectRequest;
import cn.foxtech.device.protocol.v1.omron.fins.core.entity.pdu.ConnectRespond;
import cn.foxtech.device.protocol.v1.omron.fins.core.entity.pdu.TransferRespond;
import cn.foxtech.device.protocol.v1.utils.HexUtils;

import java.util.Map;

/**
 * 链路层的编码：约定包含下面指定函数名称和格式的类型
 * 创建链路请求的编码:encodeCreateLinkerRequest
 * 创建链路响应的解码:decodeCreateLinkerRespond
 * 链路心跳请求的编码:encodeActiveLinkerRequest
 * 链路心跳响应的编码:decodeActiveLinkerRespond
 */
@FoxEdgeDeviceType(value = "omron-fins", manufacturer = "欧姆龙")
public class LinkerEncoder {
    /**
     * 创建链路请求的编码
     *
     * @param param Channel中的配置参数
     * @return 发送给设备的编码
     * @throws Exception 异常错误
     */
    @FoxEdgeLinkerAction(value = FoxEdgeLinkerAction.CREATE_LINKER_REQUEST)
    public static Object encodeCreateLinkerRequest(Map<String, Object> param) throws Exception {
        Integer clientNode = (Integer) param.get("clientNode");

        ConnectRequest session = new ConnectRequest();
        session.setClientNode(clientNode);
        byte[] pack = PduEncoder.encodePduPack(session);
        return HexUtils.byteArrayToHexString(pack);
    }

    /**
     * 创建链路响应的解码
     *
     * @param param   Channel中的配置参数
     * @param respond 从设备返回的编码
     * @return 设备是否告知正确连接上了
     * @throws Exception 异常错误
     */
    @FoxEdgeLinkerAction(value = FoxEdgeLinkerAction.CREATE_LINKER_RESPOND)
    public static boolean decodeCreateLinkerRespond(Map<String, Object> param, Object respond) throws Exception {
        byte[] pack = HexUtils.hexStringToByteArray((String) respond);
        ConnectRespond connectRespond = PduEncoder.decodePdu(pack, ConnectRespond.class);
        return true;
    }

    /**
     * 链路心跳请求的编码
     *
     * @param param Channel中的配置参数
     * @return 发送给设备的编码
     * @throws Exception 异常错误
     */
    @FoxEdgeLinkerAction(value = FoxEdgeLinkerAction.ACTIVE_LINKER_REQUEST)
    public static Object encodeActiveLinkerRequest(Map<String, Object> param) throws Exception {
        return "";
    }

    /**
     * 链路心跳响应的解码
     *
     * @param param   Channel中的配置参数
     * @param respond 从设备返回的编码
     * @return 设备是否告知是否心跳正确
     * @throws Exception 异常错误
     */
    @FoxEdgeLinkerAction(value = FoxEdgeLinkerAction.ACTIVE_LINKER_RESPOND)
    public static boolean decodeActiveLinkerRespond(Map<String, Object> param, Object respond) throws Exception {
        byte[] pack = HexUtils.hexStringToByteArray((String) respond);
        TransferRespond transferRespond = PduEncoder.decodePdu(pack, TransferRespond.class);
        Respond endCode = DataEncoder.decodeEndCode(transferRespond.getData());

        StringBuilder sb = new StringBuilder();
        Respond.checkEndCode(endCode.getMain(), endCode.getSub(), sb);

        return (endCode.getMain() == 0 && endCode.getSub() == 0);
    }

    /**
     * 拦截响应，判断链路连接状态
     *
     * @param param   Channel中的配置参数
     * @param respond 从设备返回的编码
     * @return 设备的响应中，是否连接正常状态。如果设备的返回中，提醒实际连接已经断开，那么要返回false
     * @throws Exception 异常错误
     */
    @FoxEdgeLinkerAction(value = FoxEdgeLinkerAction.INTERCEPT_LINKER_RESPOND)
    public static boolean decodeInterceptLinkerRespond(Map<String, Object> param, Object respond) throws Exception {
        byte[] pack = HexUtils.hexStringToByteArray((String) respond);
        TransferRespond transferRespond = PduEncoder.decodePdu(pack, TransferRespond.class);
        Respond endCode = DataEncoder.decodeEndCode(transferRespond.getData());

        StringBuilder sb = new StringBuilder();
        Respond.checkEndCode(endCode.getMain(), endCode.getSub(), sb);
        return true;
    }
}
