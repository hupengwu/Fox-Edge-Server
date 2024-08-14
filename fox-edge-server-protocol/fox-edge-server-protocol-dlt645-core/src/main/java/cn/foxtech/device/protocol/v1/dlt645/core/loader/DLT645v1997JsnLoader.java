/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.dlt645.core.loader;

import cn.foxtech.device.protocol.v1.dlt645.core.entity.DLT645DataEntity;
import cn.foxtech.device.protocol.v1.dlt645.core.entity.DLT645DataFormatEntity;
import cn.foxtech.device.protocol.v1.dlt645.core.entity.DLT645v1997DataEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HoldingRegisters数据实体的数据模板
 */
@Data
public class DLT645v1997JsnLoader {
    /**
     * 从CSV文件中装载映射表
     */
    public List<DLT645DataEntity> loadJsnModel(List<Map<String, Object>> rows) {
        // 将文件记录组织到map中
        List<DLT645DataEntity> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            try {
                DLT645v1997DataEntity entity = new DLT645v1997DataEntity();
                entity.setName((String) row.get("name"));
                entity.setDi1h((byte) Integer.parseInt(row.get("di1h").toString(), 16));
                entity.setDi1l((byte) Integer.parseInt(row.get("di1l").toString(), 16));
                entity.setDi0h((byte) Integer.parseInt(row.get("di0h").toString(), 16));
                entity.setDi0l((byte) Integer.parseInt(row.get("di0l").toString(), 16));
                entity.setLength(Integer.parseInt(row.get("length").toString(), 16));
                entity.setUnit(row.get("unit").toString());
                entity.setRead(Boolean.parseBoolean((row.get("read").toString())));
                entity.setWrite(Boolean.parseBoolean((row.get("write").toString())));

                DLT645DataFormatEntity format = new DLT645DataFormatEntity();
                if (format.decodeFormat((String) row.get("format"), entity.getLength())) {
                    entity.setFormat(format);
                } else {
                    System.out.println("DLT645的Format错误:" + row.get("name") + ":" + row.get("format"));
                    continue;
                }

                list.add(entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return list;
    }
}
