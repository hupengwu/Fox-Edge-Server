package cn.foxtech.device.protocol.core.utils;

import cn.foxtech.device.protocol.core.constants.FoxEdgeConstant;

import java.util.List;
import java.util.Map;

public class FoxEdgeUtils {
    /**
     * 为每条记录打上记录类型标记
     * @param recordType 记录的类型
     * @param recordList 记录的数据
     * @return 记录的列表
     */
    public static List<Map<String, Object>> makeRecordTypeTag(String recordType, List<Map<String, Object>> recordList) {
        for (Map<String, Object> record : recordList) {
            record.put(FoxEdgeConstant.RECORD_TYPE_TAG, recordType);
        }

        return recordList;
    }
}
