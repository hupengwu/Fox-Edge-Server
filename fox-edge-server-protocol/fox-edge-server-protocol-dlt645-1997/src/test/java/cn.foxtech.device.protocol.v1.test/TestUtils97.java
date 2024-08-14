/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 *
 *     This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * --------------------------------------------------------------------------- */
 
package cn.foxtech.device.protocol.v1.test;

import cn.foxtech.device.protocol.v1.dlt645.v1997.DLT645v1997ProtocolReadData;
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
        List<DLT645DataEntity> entityList = template.loadCsvFile("DLT645-v1997/v1/DLT645-1997.csv");
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
        param.put("deviceAddress", "351253111111");
        param.put("objectName", "(当前)正向有功总电能");
        param.put("operate_name", "读数据");
        param.put("tableName", "DLT645-v1997/v1/DLT645-1997.csv");
        String r = DLT645v1997ProtocolReadData.packReadData(param);

        String hex = "FE FE FE FE 68 11 11 11 53 12 35 68 81 07 43 C3 BC 76 46 33 34 0A 16  ";
        Map<String, Object> param1 = DLT645v1997ProtocolReadData.unpackReadData(hex, param);

        for (byte by : data) {
            int v = ((by & 0xff) - 0x33);
            v = 0;
        }


    }

}
