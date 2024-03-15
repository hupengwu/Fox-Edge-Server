package cn.foxtech.device.protocol.v1.test;

import cn.foxtech.device.protocol.v1.dlt645.DLT645v1997ProtocolReadData;
import cn.foxtech.device.protocol.v1.dlt645.core.DLT645Define;
import cn.foxtech.device.protocol.v1.dlt645.core.DLT645Protocol;
import cn.foxtech.device.protocol.v1.dlt645.core.entity.DLT645DataEntity;
import cn.foxtech.device.protocol.v1.dlt645.core.entity.DLT645FunEntity;
import cn.foxtech.device.protocol.v1.dlt645.core.entity.DLT645v1997DataEntity;
import cn.foxtech.device.protocol.v1.dlt645.core.loader.DLT645v1997CsvLoader;
import cn.foxtech.device.protocol.v1.utils.ContainerUtils;
import cn.foxtech.device.protocol.v1.utils.HexUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestUtils97 {
    public static void main(String[] args) {

        DLT645v1997CsvLoader template = new DLT645v1997CsvLoader();
        List<DLT645DataEntity> entityList = template.loadCsvFile("DLT645-1997.csv");
        Map<String, DLT645DataEntity> nameMap = ContainerUtils.buildMapByKey(entityList, DLT645v1997DataEntity::getDIn);
        Map<String, DLT645DataEntity> dinMap = ContainerUtils.buildMapByKey(entityList, DLT645v1997DataEntity::getKey);

        byte[] arrCmd = HexUtils.hexStringToByteArray("FE FE FE FE 68 97 39 00 42 18 27 68 81 09 65 F3 CA 6C 33 75 4B 5A 36 BC 16   ");
        Map<String, Object> result = DLT645Protocol.unPackCmd2Map(arrCmd);
        Object func = result.get(DLT645Protocol.FUN);
        DLT645FunEntity entity = DLT645FunEntity.decodeEntity((byte) func);
        String msg = entity.getMessage(DLT645Define.PRO_VER_1997);
        byte[] data = (byte[]) result.get(DLT645Protocol.DAT);
        DLT645v1997DataEntity dataEntity = new DLT645v1997DataEntity();
        dataEntity.decodeValue(data, dinMap);

        Map<String, Object> param = new HashMap<>();
        param.put("device_addr", "351253111111");
        param.put("object_name", "(当前)正向有功总电能");
        param.put("operate_name", "读数据");
        param.put("table_name", "DLT645-1997.csv");
        String r = DLT645v1997ProtocolReadData.packReadData(param);

        String hex = "FE FE FE FE 68 11 11 11 53 12 35 68 81 07 43 C3 BC 76 46 33 34 0A 16  ";
        Map<String, Object> param1 = DLT645v1997ProtocolReadData.unpackReadData(hex, param);

        for (byte by : data) {
            int v = ((by & 0xff) - 0x33);
            v = 0;
        }


    }

}
