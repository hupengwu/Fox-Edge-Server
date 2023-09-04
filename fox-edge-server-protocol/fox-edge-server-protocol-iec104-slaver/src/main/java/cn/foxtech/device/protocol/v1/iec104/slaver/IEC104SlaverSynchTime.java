package cn.foxtech.device.protocol.v1.iec104.slaver;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperateParam;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.core.utils.JsonUtils;
import cn.foxtech.device.protocol.v1.iec104.core.builder.ApduVOBuilder;
import cn.foxtech.device.protocol.v1.iec104.core.encoder.ValueEncoder;
import cn.foxtech.device.protocol.v1.iec104.core.entity.*;
import cn.foxtech.device.protocol.v1.iec104.core.enums.AsduTypeIdEnum;
import cn.foxtech.device.protocol.v1.iec104.core.enums.CotReasonEnum;
import cn.foxtech.device.protocol.v1.iec104.core.vo.ApduVO;
import cn.foxtech.device.protocol.v1.iec104.core.entity.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对时的编码解码器
 */
@FoxEdgeDeviceType(value = "IEC104 Device", manufacturer = "Fox Edge")
public class IEC104SlaverSynchTime {
    /**
     * 总召唤的编码器
     *
     * @param param 设备参数
     * @return 报文对象
     */
    @FoxEdgeOperate(name = "时间同步", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
    @FoxEdgeOperateParam(names = {"设备地址"}, values = {"1"})
    public static Object encodeSessionRequest(Map<String, Object> param) {
        try {
            // I帧控制头
            IControlEntity controlEntity = new IControlEntity();
            controlEntity.setSend((short) 0);
            controlEntity.setAccept((short) 0);

            ApduEntity apduEntity = new ApduEntity();
            apduEntity.setControl(controlEntity);

            AsduEntity asduEntity = new AsduEntity();
            apduEntity.setAsdu(asduEntity);

            // 时钟同步的类型ID
            asduEntity.setTypeId(AsduTypeIdEnum.synchronizeDatetime.getValue());

            // 非连续的数据：总召唤会要求带回各种数据，就不可能连续成一个数组
            VsqEntity vsq = new VsqEntity();
            vsq.setSq(false);
            vsq.setNum(1);
            asduEntity.setVsq(vsq);

            // 激活
            CotEntity cot = new CotEntity();
            cot.setAddr(0);
            cot.setTest(false);
            cot.setPn(true);
            cot.setReason(CotReasonEnum.active.getValue());
            asduEntity.setCot(cot);

            // 通用地址：1
            asduEntity.setCommonAddress(1);

            // 构造信息体
            Date date = new Date();
            byte[] cp56Time2a = ValueEncoder.encodeCP56Time2a(date);
            Map<Integer, byte[]> body = new HashMap<>();
            body.put(0, cp56Time2a);
            byte[] data = ValueEncoder.encodeInfoBody(body);
            asduEntity.setData(data);

            // 实体转换为VO
            ApduVO apduVO = ApduVOBuilder.buildVO(apduEntity);


            return apduVO;
        } catch (Exception e) {
            throw new ProtocolException("编码错误：" + e.getMessage());
        }
    }

    /**
     * 总召唤的解码器
     *
     * @param respond channel返回的设备响应对象
     * @param param 设备参数信息
     * @return 解码出来的设备数据
     */
    @FoxEdgeOperate(name = "时间同步", polling = true, type = FoxEdgeOperate.decoder, timeout = 10000)
    @FoxEdgeOperateParam(names = {"设备地址"}, values = {"1"})
    public static Map<String, Object> decodeSessionRequest(Object respond, Map<String, Object> param) {
        Map<String, Object> values = new HashMap<>();


        // 对收到的一批分组报文，使用模板进行解码
        List<Map<String, Object>> apduMapList = (List<Map<String, Object>>) respond;
        for (Map<String, Object> apduMap : apduMapList) {
            // 数据结构的转换：MAP->VO->Entity
            ApduVO apduVO = JsonUtils.buildObjectWithoutException(apduMap, ApduVO.class);
            ApduEntity apduEntity = ApduVOBuilder.buildEntity(apduVO);

            AsduEntity asduEntity = apduEntity.getAsdu();
            if (asduEntity == null) {
                continue;
            }


            values.putAll(decodeSntp(asduEntity));
        }

        return values;
    }

    /**
     * 单字节的单点遥测信息的解码
     *
     * @param asduEntity
     * @return
     */
    private static Map<String, Object> decodeSntp(AsduEntity asduEntity) {
        Map<String, Object> values = new HashMap<>();

        VsqEntity vsq = asduEntity.getVsq();
        CotEntity cot = asduEntity.getCot();
        byte[] data = asduEntity.getData();

        // 3字节起始地址+N个单字节的布尔量
        if (CotReasonEnum.activeConfirmed.equals(cot.getReason())) {
            if (data.length != 10) {
                throw new ProtocolException("返回的单点信息数量不正确");
            }

            int address = data[0] & 0xff;
            address += (data[1] & 0xff) * 0x100;
            address += (data[2] & 0xff) * 0x10000;

            Map<Integer, byte[]> bodys = ValueEncoder.decodeInfoBody(data, 7);
            if (bodys == null) {
                throw new ProtocolException("返回的信息体不正确！");
            }

            byte[] cp56Time2a = bodys.get(0);
            if (cp56Time2a == null) {
                throw new ProtocolException("返回的地址不正确！");
            }

            Date date = ValueEncoder.decodeCP56Time2a(cp56Time2a);
            values.put("datetime", date.getTime());
        }


        return values;
    }
}
