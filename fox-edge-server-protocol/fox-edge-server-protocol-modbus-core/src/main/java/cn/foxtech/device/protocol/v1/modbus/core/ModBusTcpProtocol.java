/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.modbus.core;

import java.util.HashMap;
import java.util.Map;

/**
 * TCP版本的解码器
 */
public class ModBusTcpProtocol extends ModBusProtocol {
    /**
     * 解包命令
     *
     * @param arrCmd 命令报文
     * @return 操作是否成功
     */
    @Override
    public ModBusEntity unPackCmd2Entity(byte[] arrCmd) {
        ModBusEntity entity = new ModBusEntity();

        // 流水号
        int wSn = (arrCmd[0] & 0xff) * 0x100;
        wSn += arrCmd[1] & 0xff;
        entity.setSn((short) (wSn & 0xffff));

        // 协议类型
        if (arrCmd[2] != 0 || arrCmd[3] != 0) {
            return null;
        }

        // 报文长度
        int len = (arrCmd[4] & 0xff) * 0x100;
        len += arrCmd[5] & 0xff;
        if (arrCmd.length != len + 6) {
            return null;
        }
        if (len < 2) {
            return null;
        }

        //地址
        entity.setDevAddr(arrCmd[6]);

        // 功能
        entity.setFunc(arrCmd[7]);

        //数据
        byte[] arrData = new byte[len - 2];
        System.arraycopy(arrCmd, 8, arrData, 0, arrData.length);
        entity.setData(arrData);

        return entity;
    }

    /**
     * 打包命令
     *
     * @return 打包是否成功
     */
    @Override
    public byte[] packCmd4Entity(ModBusEntity entity) {
        byte[] arrCmd = new byte[entity.getData().length + 8];

        // 流水号
        arrCmd[0] = (byte) ((entity.getSn() & 0xff00) >> 8);
        arrCmd[1] = (byte) (entity.getSn() & 0xff);

        // 协议类型
        arrCmd[2] = 0;
        arrCmd[3] = 0;

        // 报文长度
        int len = entity.getData().length + 2;
        arrCmd[4] = (byte) ((len & 0xff00) >> 8);
        arrCmd[5] = (byte) (len & 0xff);

        //地址
        arrCmd[6] = entity.getDevAddr();

        // 功能
        arrCmd[7] = entity.getFunc();

        //数据
        System.arraycopy(entity.getData(), 0, arrCmd, 8, entity.getData().length);

        return arrCmd;
    }

    /**
     * 检查参数是否完备
     *
     * @param param
     * @return
     */
    public boolean checkParam(Map<String, Object> param) {
        // 检查：设备地址/功能码/数据是否功能完备
        if (!super.checkParam(param)) {
            return false;
        }

        // TCP方式还要求SN
        return param.containsKey(ModBusConstants.SN) && param.get(ModBusConstants.SN) instanceof Integer;
    }

    /**
     * 包装成map参数格式
     *
     * @param param
     * @return
     */
    @Override
    public byte[] packCmd4Map(Map<String, Object> param) {
        // 检查参数是否完备
        if (!this.checkParam(param)) {
            return null;
        }

        ModBusEntity entity = new ModBusEntity();
        entity.setSn(Integer.decode(param.get(ModBusConstants.SN).toString()));
        entity.setDevAddr(Integer.decode(param.get(ModBusConstants.ADDR).toString()).byteValue());
        entity.setFunc(Integer.decode(param.get(ModBusConstants.FUNC).toString()).byteValue());
        entity.setData(((byte[]) param.get(ModBusConstants.DATA)));

        return packCmd4Entity(entity);
    }

    /**
     * 解码成MAP格式
     *
     * @param arrCmd
     * @return
     */
    @Override
    public Map<String, Object> unPackCmd2Map(byte[] arrCmd) {
        ModBusEntity entity = unPackCmd2Entity(arrCmd);
        if (entity == null) {
            return null;
        }

        Map<String, Object> value = new HashMap<>();
        value.put(ModBusConstants.SN, entity.getSn());
        value.put(ModBusConstants.ADDR, entity.getDevAddr());
        value.put(ModBusConstants.FUNC, entity.getFunc());
        value.put(ModBusConstants.DATA, entity.getData());
        return value;
    }
}
