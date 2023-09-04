package cn.foxtech.device.protocol.v1.modbus.core;


import java.util.HashMap;
import java.util.Map;

/**
 * 工业行业通信协议
 * MODBUS是工业设备的常用标准协议
 * 背景知识：
 * 1、它的基本设计理念是把设备当作一片连续的内存表，往内存地址上读取数据，设备就返回相应的设备检测数据
 * 往内存地址上写入数据，设备就配置或者控制动作。
 * 2、ModBus设备有两种工作模式，一种是ASCII，一种是RTU，上位机要用相应的工作模式去对接
 */
public abstract class ModBusProtocol {

    /**
     * 打包：将实体打包成报文
     *
     * @param entity 实体
     * @return 数据报文
     */
    public abstract byte[] packCmd4Entity(ModBusEntity entity);

    /**
     * 解包：将报文解码成实体
     *
     * @param arrCmd 报文
     * @return 实体
     */
    public abstract ModBusEntity unPackCmd2Entity(byte[] arrCmd);

    /**
     * 包装成map参数格式
     *
     * @param param
     * @return
     */
    public abstract byte[] packCmd4Map(Map<String, Object> param);

    /**
     * 解码成成map参数格式
     *
     * @param arrCmd 数据报文
     * @return
     */
    public abstract Map<String, Object> unPackCmd2Map(byte[] arrCmd);

    /**
     * 检查参数是否完备
     *
     * @param param
     * @return
     */
    public boolean checkParam(Map<String, Object> param) {
        if (!param.containsKey(ModBusConstants.ADDR) || !(param.get(ModBusConstants.ADDR) instanceof Byte || param.get(ModBusConstants.ADDR) instanceof Integer)) {
            return false;
        }

        if (!param.containsKey(ModBusConstants.FUNC) || !(param.get(ModBusConstants.FUNC) instanceof Byte || param.get(ModBusConstants.FUNC) instanceof Integer)) {
            return false;
        }

        return param.containsKey(ModBusConstants.DATA) && param.get(ModBusConstants.DATA) instanceof byte[];
    }

    public byte[] packCmdReadInputRegisters4Map(Map<String, Object> param) {
        return packCmdReadRegisters4Map((byte) 0x04, param);
    }

    /**
     * 读取:保持寄存器
     *
     * @param param 设备地址/地址偏移量/寄存器数量/模式
     * @return
     */
    public byte[] packCmdReadHoldingRegisters4Map(Map<String, Object> param) {
        return packCmdReadRegisters4Map((byte) 0x03, param);
    }

    public byte[] packCmdReadRegisters4Map(byte func, Map<String, Object> param) {
        if (!param.containsKey(ModBusConstants.MODE) || !(param.get(ModBusConstants.MODE) instanceof String)) {
            return null;
        }

        if (!param.containsKey(ModBusConstants.ADDR) || !(param.get(ModBusConstants.ADDR) instanceof Byte || param.get(ModBusConstants.ADDR) instanceof Integer)) {
            return null;
        }

        if (!param.containsKey(ModBusConstants.REG_ADDR) || !(param.get(ModBusConstants.REG_ADDR) instanceof Byte || param.get(ModBusConstants.REG_ADDR) instanceof Integer)) {
            return null;
        }

        if (!param.containsKey(ModBusConstants.REG_CNT) || !(param.get(ModBusConstants.REG_CNT) instanceof Byte || param.get(ModBusConstants.REG_CNT) instanceof Integer)) {
            return null;
        }

        ModBusReadRegistersRequest request = new ModBusReadRegistersRequest();
        request.getEntity().setDevAddr(Byte.decode(param.get(ModBusConstants.ADDR).toString()));
        request.setMemAddr(Integer.decode(param.get(ModBusConstants.REG_ADDR).toString()));
        request.setCount(Integer.decode(param.get(ModBusConstants.REG_CNT).toString()));
        if (param.containsKey(ModBusConstants.SN)) {
            request.getEntity().setSn(Short.decode(param.get(ModBusConstants.SN).toString()));
        }

        request.getEntity().setFunc(func);
        return packCmdReadRegisters4Request(request);
    }


    /**
     * 解码
     *
     * @param arrCmd
     * @return
     */
    public Map<String, Object> unPackCmdReadHoldingRegisters2Map(byte[] arrCmd) {
        ModBusReadRegistersRespond respond = this.unPackCmdReadRegisters2Respond(arrCmd);
        if (respond == null) {
            return null;
        }

        Map<String, Object> value = new HashMap<>();
        value.put(ModBusConstants.ADDR, respond.getEntity().getDevAddr());
        value.put(ModBusConstants.REG_HOLD_STATUS, respond.getStatus());
        return value;
    }

    /**
     * 读取线圈状态的编码函数
     *
     * @param param 设备地址/地址偏移量/线圈数量
     * @return
     */
    public byte[] packCmdReadCoilStatus4Map(Map<String, Object> param) {
        return this.packCmdReadStatus4Map((byte) 0x01, param);
    }

    public byte[] packCmdReadInputStatus4Map(Map<String, Object> param) {
        return this.packCmdReadStatus4Map((byte) 0x02, param);
    }

    public byte[] packCmdReadStatus4Map(byte func, Map<String, Object> param) {
        if (!param.containsKey(ModBusConstants.MODE) || !(param.get(ModBusConstants.MODE) instanceof String)) {
            return null;
        }

        if (!param.containsKey(ModBusConstants.ADDR) || !(param.get(ModBusConstants.ADDR) instanceof Byte || param.get(ModBusConstants.ADDR) instanceof Integer)) {
            return null;
        }

        if (!param.containsKey(ModBusConstants.REG_ADDR) || !(param.get(ModBusConstants.REG_ADDR) instanceof Byte || param.get(ModBusConstants.REG_ADDR) instanceof Integer)) {
            return null;
        }

        if (!param.containsKey(ModBusConstants.REG_CNT) || !(param.get(ModBusConstants.REG_CNT) instanceof Byte || param.get(ModBusConstants.REG_CNT) instanceof Integer)) {
            return null;
        }

        ModBusReadStatusRequest request = new ModBusReadStatusRequest();
        request.getEntity().setDevAddr(Byte.decode(param.get(ModBusConstants.ADDR).toString()));
        request.setMemAddr(Integer.decode(param.get(ModBusConstants.REG_ADDR).toString()));
        request.setCount(Integer.decode(param.get(ModBusConstants.REG_CNT).toString()));
        if (param.containsKey(ModBusConstants.SN)) {
            request.getEntity().setSn(Short.decode(param.get(ModBusConstants.SN).toString()));
        }

        request.getEntity().setFunc(func);
        return this.packCmdReadStatus4Request(request);
    }


    /**
     * 读取保持寄存器
     *
     * @return 是否成功
     */
    public byte[] packCmdReadRegisters4Request(ModBusReadRegistersRequest request) {
        byte[] arrData = new byte[4];

        arrData[0] = (byte) (request.getMemAddr() / 0x100);
        arrData[1] = (byte) (request.getMemAddr() % 0x100);
        arrData[2] = (byte) (request.getCount() / 0x100);
        arrData[3] = (byte) (request.getCount() % 0x100);

        request.getEntity().setData(arrData);

        return packCmd4Entity(request.getEntity());
    }

    public ModBusReadRegistersRespond unPackCmdReadRegisters2Respond(byte[] arrCmd) {
        ModBusEntity entity = unPackCmd2Entity(arrCmd);
        if (entity == null) {
            return null;
        }

        ModBusReadRegistersRespond respond = new ModBusReadRegistersRespond();
        respond.setEntity(entity);

        if (respond.getEntity().getFunc() == (byte) 0x83 && respond.getEntity().getData().length > 0) {
            respond.getEntity().setErrCode(respond.getEntity().getData()[0]);
            respond.getEntity().setErrMsg(ModBusError.getError(respond.getEntity().getData()[0]));
            return null;
        }
        // 检测是否为输入03和04的返回
        if (respond.getEntity().getFunc() != 0x03 && respond.getEntity().getFunc() != 0x04) {
            return null;
        }

        byte[] arrData = respond.getEntity().getData();
        int iDataSize = arrData.length;
        if (iDataSize < 1) {
            return null;
        }
        if ((arrData[0] & 0xff) != iDataSize - 1) {
            return null;
        }
        if (iDataSize % 2 != 1) {
            return null;
        }


        // 初始化数组大小
        short wCount = (short) ((iDataSize - 1) / 2);
        respond.setStatus(new int[wCount]);
        int[] arrStatus = respond.getStatus();

        // byte *byAt = arrData.GetData()+1;
        int byAt = 1;
        // short *byStatus = arrStatus.GetData();
        int byStatus = 0;
        for (int i = 0; i < wCount; i++) {
            int status = (arrData[byAt] & 0xff) * 0x100;
            status += arrData[byAt + 1] & 0xFF;

            arrStatus[byStatus++] = status;
            byAt += 2;
        }

        return respond;
    }

    /**
     * 读取保持寄存器
     *
     * @return 是否成功
     */
    public byte[] packCmdWriteRegisters4Request(ModBusWriteRegistersRequest request) {
        byte[] arrData = new byte[4];


        arrData[0] = (byte) (request.getMemAddr() / 0x100);
        arrData[1] = (byte) (request.getMemAddr() % 0x100);
        arrData[2] = (byte) (request.getValue() / 0x100);
        arrData[3] = (byte) (request.getValue() % 0x100);

        request.getEntity().setFunc((byte) 0x06);
        request.getEntity().setData(arrData);

        return packCmd4Entity(request.getEntity());
    }

    public byte[] packCmdReadStatus4Request(ModBusReadStatusRequest request) {
        byte[] arrData = new byte[4];

        arrData[0] = (byte) (request.getMemAddr() / 0x100);
        arrData[1] = (byte) (request.getMemAddr() % 0x100);
        arrData[2] = (byte) (request.getCount() / 0x100);
        arrData[3] = (byte) (request.getCount() % 0x100);
        request.getEntity().setData(arrData);

        return packCmd4Entity(request.getEntity());
    }

    public ModBusWriteRegistersRespond unPackCmdWriteRegisters2Respond(byte[] arrCmd) {
        ModBusEntity entity = unPackCmd2Entity(arrCmd);
        if (entity == null) {
            return null;
        }

        ModBusWriteRegistersRespond respond = new ModBusWriteRegistersRespond();
        respond.setEntity(entity);


        if (respond.getEntity().getFunc() == (byte) 0x86 && respond.getEntity().getData().length > 0) {
            respond.getEntity().setErrCode(respond.getEntity().getData()[0]);
            respond.getEntity().setErrMsg(ModBusError.getError(respond.getEntity().getData()[0]));
            return null;
        }
        if (respond.getEntity().getFunc() != 0x06) {
            return null;
        }

        byte[] arrData = respond.getEntity().getData();
        if (arrData.length != 4) {
            return null;
        }

        respond.setMemAddr(((arrData[0] & 0xff) * 0x100) + (arrData[1] & 0xff));
        respond.setValue(((arrData[2] & 0xff) * 0x100) + (arrData[3] & 0xff));


        return respond;
    }

    public ModBusReadStatusRespond unPackCmdReadStatus2Respond(byte[] arrCmd) {
        ModBusEntity entity = unPackCmd2Entity(arrCmd);
        if (entity == null) {
            return null;
        }

        ModBusReadStatusRespond respond = new ModBusReadStatusRespond();
        respond.setEntity(entity);

        byte byFunc = respond.getEntity().getFunc();
        if ((byFunc & 0xf0) == 0x80 && respond.getEntity().getData().length > 0) {
            respond.getEntity().setErrCode(respond.getEntity().getData()[0]);
            respond.getEntity().setErrMsg(ModBusError.getError(respond.getEntity().getData()[0]));
            return null;
        }
        // 两种状态
        if (byFunc != 0x01 && byFunc != 0x02) {
            return null;
        }

        byte[] arrData = respond.getEntity().getData();
        int iDataSize = arrData.length;
        if (iDataSize < 1) {
            return null;
        }
        if (arrData[0] != iDataSize - 1) {
            return null;
        }
        int wCount = respond.getEntity().getData()[0];

        if ((wCount + 1) != arrData.length) {
            return null;
        }

        // 初始化数组大小
        respond.setStatus(new boolean[wCount * 8]);
        boolean[] arrStatus = respond.getStatus();

        int byStatus = 0;
        for (int i = 1; i < iDataSize; i++) {
            byte byAt = arrData[i];
            byte byBit = 0x01;

            for (int k = 0; k < 8; k++) {
                arrStatus[byStatus++] = (byte) (byAt & byBit) != 0;

                byBit <<= 1;
            }
        }

        return respond;
    }


    public byte[] packCmdWriteStatus4Request(ModBusWriteStatusRequest request) {
        byte[] arrData = new byte[4];

        arrData[0] = (byte) (request.getMemAddr() / 0x100);
        arrData[1] = (byte) (request.getMemAddr() % 0x100);
        if (request.isStatus()) {
            arrData[2] = (byte) (0xff);
            arrData[3] = (byte) (0x00);
        } else {
            arrData[2] = (byte) (0x00);
            arrData[3] = (byte) (0x00);
        }
        request.getEntity().setData(arrData);

        request.getEntity().setFunc((byte) 0x05);
        return packCmd4Entity(request.getEntity());
    }

    public ModBusWriteStatusRespond unPackCmdWriteStatus2Respond(byte[] arrCmd) {
        ModBusEntity entity = unPackCmd2Entity(arrCmd);
        if (entity == null) {
            return null;
        }

        ModBusWriteStatusRespond respond = new ModBusWriteStatusRespond();
        respond.setEntity(entity);

        byte byFunc = respond.getEntity().getFunc();
        if ((byFunc & 0xf0) == 0x80 && respond.getEntity().getData().length > 0) {
            respond.getEntity().setErrCode(respond.getEntity().getData()[0]);
            respond.getEntity().setErrMsg(ModBusError.getError(respond.getEntity().getData()[0]));
            return null;
        }
        if (byFunc != 0x05) {
            return null;
        }

        byte[] arrData = respond.getEntity().getData();
        int iDataSize = arrData.length;
        if (iDataSize != 4) {
            return null;
        }

        respond.setMemAddr(((arrData[0] & 0xff) * 0x100) + (arrData[1] & 0xff));
        respond.setStatus((arrData[2] & 0xff) == 0xff);


        return respond;
    }

    /**
     * 读取线圈状态的编码函数
     *
     * @return 是否成功
     */
    public byte[] packCmdReadCoilStatus4Request(ModBusReadStatusRequest request) {
        request.getEntity().setFunc((byte) 0x01);
        return this.packCmdReadStatus4Request(request);
    }

    /**
     * 读取线圈状态：
     * 注意：线圈状态返回的数量总是8的倍数，也就是说有些数据是空白的，需要跟发送参数核对。
     * 比如，发送包查询20个数据，那么会返回24个数据，实际上的数据要跟发送报文中的20个核对
     *
     * @param arrCmd
     * @return
     */
    public ModBusReadStatusRespond unPackCmdReadCoilStatus2Respond(byte[] arrCmd) {
        ModBusReadStatusRespond respond = this.unPackCmdReadStatus2Respond(arrCmd);
        if (respond == null) {
            return null;
        }

        if (respond.getEntity().getFunc() == 0x01) {
            return respond;
        }

        return null;
    }


    public byte[] packCmdReadInputStatus4Request(ModBusReadStatusRequest request) {
        request.getEntity().setFunc((byte) 0x02);
        return this.packCmdReadStatus4Request(request);
    }

    public ModBusReadStatusRespond unPackCmdReadInputStatus2Respond(byte[] arrCmd) {
        ModBusReadStatusRespond respond = this.unPackCmdReadStatus2Respond(arrCmd);
        if (respond == null) {
            return null;
        }
        if (respond.getEntity().getFunc() == 0x02) {
            return respond;
        }

        return null;
    }


    /**
     * 读取保持寄存器
     *
     * @return 是否成功
     */
    public byte[] packCmdReadHoldingRegisters4Request(ModBusReadRegistersRequest request) {
        request.getEntity().setFunc((byte) 0x03);
        return this.packCmdReadRegisters4Request(request);
    }

    public byte[] packCmdReadInputRegisters4Request(ModBusReadRegistersRequest request) {
        request.getEntity().setFunc((byte) 0x04);
        return this.packCmdReadRegisters4Request(request);
    }

    public ModBusReadRegistersRespond unPackCmdReadHoldingRegisters2Respond(byte[] arrCmd) {
        ModBusReadRegistersRespond respond = this.unPackCmdReadRegisters2Respond(arrCmd);
        if (respond == null) {
            return null;
        }
        if (respond.getEntity().getFunc() != 0x03) {
            return null;
        }

        return respond;
    }

    public ModBusReadRegistersRespond unPackCmdReadInputRegisters2Respond(byte[] arrCmd) {
        ModBusReadRegistersRespond respond = this.unPackCmdReadRegisters2Respond(arrCmd);
        if (respond == null) {
            return null;
        }
        if (respond.getEntity().getFunc() != 0x04) {
            return null;
        }

        return respond;
    }

    public byte[] packCmdWriteSingleCoilStatus4Request(ModBusWriteStatusRequest request) {
        request.getEntity().setFunc((byte) 0x05);
        return this.packCmdWriteStatus4Request(request);
    }

    public ModBusWriteStatusRespond unPackCmdWriteSingleCoilStatus2Respond(byte[] arrCmd) {
        ModBusWriteStatusRespond respond = this.unPackCmdWriteStatus2Respond(arrCmd);
        if (respond == null) {
            return null;
        }
        if (respond.getEntity().getFunc() == 0x05) {
            return respond;
        }

        return null;
    }


    /**
     * 写单个保持寄存器
     *
     * @param request 请求参数
     * @return 报文
     */
    public byte[] packCmdWriteHoldingRegisters4Request(ModBusWriteRegistersRequest request) {
        request.getEntity().setFunc((byte) 0x06);
        return this.packCmdWriteRegisters4Request(request);
    }

    /**
     * 写单个保持寄存器
     *
     * @param arrCmd 报文
     * @return 是否成功
     */
    public ModBusWriteRegistersRespond unPackCmdWriteHoldingRegisters2Respond(byte[] arrCmd) {
        ModBusWriteRegistersRespond respond = this.unPackCmdWriteRegisters2Respond(arrCmd);
        if (respond == null) {
            return null;
        }
        if (respond.getEntity().getFunc() == 0x06) {
            return respond;
        }

        return null;
    }

}
