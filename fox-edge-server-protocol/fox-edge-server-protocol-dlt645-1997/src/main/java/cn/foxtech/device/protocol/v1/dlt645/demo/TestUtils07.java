package cn.foxtech.device.protocol.v1.dlt645.demo;

import cn.foxtech.device.protocol.v1.dlt645.core.DLT645Define;
import cn.foxtech.device.protocol.v1.dlt645.core.DLT645Protocol;
import cn.foxtech.device.protocol.v1.dlt645.core.entity.DLT645DataEntity;
import cn.foxtech.device.protocol.v1.dlt645.core.entity.DLT645FunEntity;
import cn.foxtech.device.protocol.v1.dlt645.core.entity.DLT645v2007DataEntity;
import cn.foxtech.device.protocol.v1.dlt645.core.loader.DLT645v2007CsvLoader;
import cn.foxtech.device.protocol.v1.utils.ContainerUtils;
import cn.foxtech.device.protocol.v1.utils.HexUtils;

import java.util.List;
import java.util.Map;

public class TestUtils07 {
    public static void main(String[] args) {

        DLT645v2007CsvLoader template = new DLT645v2007CsvLoader();
        List<DLT645DataEntity> entityList = template.loadCsvFile("DLT645-2007.csv");
        Map<String, DLT645DataEntity> nameMap = ContainerUtils.buildMapByKey(entityList, DLT645v2007DataEntity::getName);
        Map<String, DLT645DataEntity> dinMap = ContainerUtils.buildMapByKey(entityList, DLT645v2007DataEntity::getKey);

        byte[] arrCmd = HexUtils.hexStringToByteArray("FE FE FE FE 68 01 00 00 00 00 00 68 91 09 33 33 34 33 6A 59 33 33 34 95 16  ");
        Map<String, Object> result = DLT645Protocol.unPackCmd2Map(arrCmd);
        Object func = result.get(DLT645Protocol.FUN);
        DLT645FunEntity entity = DLT645FunEntity.decodeEntity((byte) func);
        String msg = entity.getMessage(DLT645Define.PRO_VER_2007);
        byte[] data = (byte[]) result.get(DLT645Protocol.DAT);
        DLT645v2007DataEntity dataEntity = new DLT645v2007DataEntity();
        dataEntity.decodeValue(data, dinMap);
        // DLT645v2007DataEntity dataEntity = entityList.get("(当前)正向有功总电能");
        //  dataEntity.decodeDIn(data);


        for (byte by : data) {
            int v = ((by & 0xff) - 0x33);
            v = 0;
        }

        arrCmd = HexUtils.hexStringToByteArray("68 11 11 11 53 12 35 68 01 02 43 C3 A6 16   ");
        result = DLT645Protocol.unPackCmd2Map(arrCmd);
        func = result.get(DLT645Protocol.FUN);
        entity = DLT645FunEntity.decodeEntity((byte) func);
        msg = entity.getMessage("v07");
        data = (byte[]) result.get(DLT645Protocol.DAT);
//        for (int i = 0; i < data.length; i++) {
//            int v = ((int)(data[i] & 0xff)) - 0x33;
//            data[i] = (byte) v;
//        }

//        DLT645DataID2007 DLT645DataID2007 = new DLT645DataID2007();
//        DLT645DataEntity dataEntity = new DLT645DataEntity();
//        dataEntity.unPackData(data, DLT645DataUtil.format_4byte_double);


        byte test = -90;
        int v = test & 0xff;
        v = (test & 0xff) - 0x33;

        arrCmd = HexUtils.hexStringToByteArray("FE FE FE FE 68 01 00 00 00 00 00 68 91 09 33 33 34 33 45 33 33 33 34 4A 16");
        result = DLT645Protocol.unPackCmd2Map(arrCmd);
        func = result.get(DLT645Protocol.FUN);
        entity = DLT645FunEntity.decodeEntity((byte) func);
        msg = entity.getMessage(DLT645Define.PRO_VER_2007);
        data = (byte[]) result.get(DLT645Protocol.DAT);

        arrCmd = HexUtils.hexStringToByteArray("FE FE FE FE 68 18 20 12 22 20 65 68 11 04 33 32 34 35 A4 16");
        result = DLT645Protocol.unPackCmd2Map(arrCmd);
        func = result.get(DLT645Protocol.FUN);
        entity = DLT645FunEntity.decodeEntity((byte) func);
        msg = entity.getMessage("v07");
        data = (byte[]) result.get(DLT645Protocol.DAT);

    }

    public static byte[] unPackData(byte[] data) {
        byte[] result = new byte[data.length];
        // 每个字节先减去0x33
        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) ((data[i] & 0xff) - 0x33);
        }


        return result;
    }
}
