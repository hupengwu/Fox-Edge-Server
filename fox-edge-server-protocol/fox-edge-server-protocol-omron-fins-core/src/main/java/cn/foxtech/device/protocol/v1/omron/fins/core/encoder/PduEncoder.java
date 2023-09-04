package cn.foxtech.device.protocol.v1.omron.fins.core.encoder;

import cn.foxtech.device.protocol.v1.omron.fins.core.entity.pdu.*;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.omron.fins.core.entity.pdu.*;
import cn.foxtech.device.protocol.v1.omron.fins.core.entity.pdu.*;

public class PduEncoder {

    private static long getLong(byte[] pack, int index) {
        long value = 0;
        value += (pack[index + 3] & 0xff) * 0x01;
        value += (pack[index + 2] & 0xff) * 0x0100;
        value += (pack[index + 1] & 0xff) * 0x010000;
        value += (pack[index + 0] & 0xff) * 0x01000000;

        return value;
    }

    private static void setLong(byte[] pack, int index, long value) {
        pack[index + 3] = (byte) (value & 0xff);
        pack[index + 2] = (byte) ((value >> 8) & 0xff);
        pack[index + 1] = (byte) ((value >> 16) & 0xff);
        pack[index + 0] = (byte) ((value >> 24) & 0xff);
    }

    /**
     * 读取设备操作
     *
     * @param entity
     * @return
     */
    public static byte[] encodePduPack(OmronFinsPdu entity) {
        byte[] pack = new byte[entity.getData().length + 16];

        // header
        setLong(pack, 0, 0x46494E53);
        // length
        setLong(pack, 4, entity.getData().length + 8);
        // command
        setLong(pack, 8, entity.getCommand());
        // error
        setLong(pack, 12, entity.getError());
        // 数据
        System.arraycopy(entity.getData(), 0, pack, 16, entity.getData().length);
        return pack;
    }

    public static OmronFinsPdu decodePduPack(byte[] pack) {
        // 最小长度检查
        if (pack.length < 16) {
            throw new ProtocolException("返回数据长度至少12字节");
        }

        // 检查：header
        long header = getLong(pack, 0);
        if (header != 0x46494E53) {
            throw new ProtocolException("header不正确!");
        }

        // 检查：length
        long length = getLong(pack, 4);
        if (length > 64 * 1024) {
            throw new ProtocolException("报文太长，不支持该报文!");
        }
        // 检查：报文长度是否正确
        if (pack.length != length + 8) {
            throw new ProtocolException("报文长度不正确！");
        }

        // 检查：command
        long command = getLong(pack, 8);
        if (command != 0 && command != 1 && command != 2) {
            throw new ProtocolException("命令码不正确！");
        }

        // errorCode
        long errorCode = getLong(pack, 12);

        // 检查数据长度
        byte[] data = new byte[pack.length - 16];
        System.arraycopy(pack, 16, data, 0, data.length);

        OmronFinsPdu entity = new OmronFinsPdu();
        entity.setCommand((int) command);
        entity.setError((int) errorCode);
        entity.setData(data);
        return entity;
    }

    /**
     * 打包连接PDU
     *
     * @return
     */
    public static byte[] encodePduPack(ConnectRequest session) {
        OmronFinsPdu entity = new OmronFinsPdu();
        entity.setCommand(0x00);

        entity.setData(new byte[4]);
        setLong(entity.getData(), 0, session.getClientNode());
        return encodePduPack(entity);
    }

    public static byte[] encodePduPack(Transfer transfer) {
        byte[] data = new byte[12 + transfer.getData().length];
        // header
        Header header = transfer.getHeader();
        data[0] = (byte) header.getICF();
        data[1] = (byte) header.getRSV();
        data[2] = (byte) header.getGCT();
        data[3] = (byte) header.getDNA();
        data[4] = (byte) header.getDA1();
        data[5] = (byte) header.getDA2();
        data[6] = (byte) header.getSNA();
        data[7] = (byte) header.getSA1();
        data[8] = (byte) header.getSA2();
        data[9] = (byte) header.getSID();
        // 控制码
        data[10] = (byte) transfer.getMrc();
        data[11] = (byte) transfer.getSrc();
        System.arraycopy(transfer.getData(), 0, data, 12, transfer.getData().length);

        // 按一级编码打包
        OmronFinsPdu entity = new OmronFinsPdu();
        entity.setCommand(0x02);
        entity.setError(0x00);
        entity.setData(data);
        return encodePduPack(entity);
    }


    public static <T> T decodePdu(byte[] pack, Class<T> clazz) {
        OmronFinsPdu entity = decodePduPack(pack);

        if (ConnectRequest.class.equals(clazz)) {
            if (entity.getCommand() != 0x00) {
                throw new ProtocolException("不匹配的报文!");
            }

            ConnectRequest session = new ConnectRequest();
            if (entity.getData().length != 4) {
                throw new ProtocolException("数据长度不正确！");
            }

            session.setClientNode((int) getLong(entity.getData(), 0));
            return (T) session;
        }

        if (ConnectRespond.class.equals(clazz)) {
            if (entity.getCommand() != 0x01) {
                throw new ProtocolException("不匹配的报文!");
            }
            ConnectRespond session = new ConnectRespond();

            if (entity.getData().length != 8) {
                throw new ProtocolException("数据长度不正确！");
            }

            session.setClientNode((int) getLong(entity.getData(), 0));
            session.setServerNode((int) getLong(entity.getData(), 4));

            return (T) session;
        }

        if (TransferRequest.class.equals(clazz) || TransferRespond.class.equals(clazz)) {
            if (entity.getCommand() != 0x02) {
                throw new ProtocolException("不匹配的报文!");
            }


            if (entity.getData().length < 12) {
                throw new ProtocolException("数据长度不正确！");
            }
            byte[] data = entity.getData();

            Transfer transfer = null;
            if (clazz.equals(TransferRequest.class)) {
                transfer = new TransferRequest();
            }
            if (clazz.equals(TransferRespond.class)) {
                transfer = new TransferRespond();
            }

            // header
            Header header = new Header();
            header.setICF(data[0] & 0xff);
            header.setRSV(data[1] & 0xff);
            header.setGCT(data[2] & 0xff);
            header.setDNA(data[3] & 0xff);
            header.setDA1(data[4] & 0xff);
            header.setDA2(data[5] & 0xff);
            header.setSNA(data[6] & 0xff);
            header.setSA1(data[7] & 0xff);
            header.setSA2(data[8] & 0xff);
            header.setSID(data[9] & 0xff);
            transfer.setHeader(header);

            // 命令码
            transfer.setMrc(data[10] & 0xff);
            transfer.setSrc(data[11] & 0xff);


            // 数据
            byte[] sdata = new byte[data.length - 12];
            transfer.setData(sdata);
            System.arraycopy(data, 12, sdata, 0, sdata.length);

            return (T) transfer;
        }

        throw new ProtocolException("这不是匹配的PDU类型!");
    }
}

