package cn.foxtech.device.simulator.mqtt.service;

import cn.foxtech.common.utils.hex.HexUtils;
import lombok.Getter;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 设备数据的模拟
 */
@Component
public class MqttSimulatorService {
    /**
     * 串口名-串口映射表
     */
    private final Map<String, String> recv2rspd = new ConcurrentHashMap<>();

    /**
     * 串口名-串口映射表
     */
    @Getter
    private final Map<String, String> channel2event = new ConcurrentHashMap();

    /**
     * 重新打开串口
     */
    public void reload() {
        try {
            // 读取JSON格式的配置文件
            File file = new File("");
            String confFileName = file.getAbsolutePath() + "/conf/fox-edge-server-device-simulator-mqtt.conf";

            String jsonData = new String(Files.readAllBytes(Paths.get(confFileName)));

            JSONObject parse = JSONObject.fromObject(jsonData);
            JSONArray array = parse.getJSONArray("device_simulator");
            for (Object object : array) {
                // 读取文件参数
                JSONObject at = (JSONObject) object;
                String name = (String) at.get("name");
                String recv = (String) at.get("recv");
                String rspd = (String) at.get("rspd");

                String channelName = (String) at.get("channel_name");
                String event = (String) at.get("event");

                if (recv != null && rspd != null) {
                    // 格式化数据
                    recv = HexUtils.byteArrayToHexString(HexUtils.hexStringToByteArray(recv)).toUpperCase();
                    rspd = HexUtils.byteArrayToHexString(HexUtils.hexStringToByteArray(rspd)).toUpperCase();

                    this.recv2rspd.put(recv, rspd);
                }
                if (channelName != null && event != null) {
                    // 格式化数据
                    event = HexUtils.byteArrayToHexString(HexUtils.hexStringToByteArray(event)).toUpperCase();

                    this.channel2event.put(channelName, event);
                }
            }
        } catch (IOException e) {
            System.out.print(e);
        }
    }

    /**
     * 查询串口数据:增加同步锁，避免并发访问带来的多线程异常。
     *
     * @param port
     * @param sednData
     * @param timeout
     * @return
     */
    public String execute(String port, String sednData, int timeout) {
        String recv = HexUtils.byteArrayToHexString(HexUtils.hexStringToByteArray(sednData)).toUpperCase();
        Map<String, String> recv2rspd = this.recv2rspd;
        if (!recv2rspd.containsKey(recv)) {
            return null;
        }

        return recv2rspd.get(recv).toUpperCase();
    }

    public Map<String, String> report() {
        for (Map.Entry<String, String> entry : this.channel2event.entrySet()) {
            Map<String, String> report = new HashMap<>();
            report.put(entry.getKey(), entry.getValue());
            return report;
        }

        return null;
    }
}
