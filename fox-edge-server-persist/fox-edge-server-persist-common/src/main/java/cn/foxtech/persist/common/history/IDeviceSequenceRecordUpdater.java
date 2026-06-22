package cn.foxtech.persist.common.history;

import java.util.Map;

public interface IDeviceSequenceRecordUpdater {
    /**
     * 更新记录类型的数据
     *
     * @param deviceName 设备名称
     * @param deviceType 设备类型
     * @param sequence   记录信息
     */
    public void updateDeviceSequenceValue(String deviceName, String manufacturer, String deviceType, Map<String, Object> sequence);

    /**
     * 清空过期的数据
     */
    public void clearDeviceSequenceRecord();
}
