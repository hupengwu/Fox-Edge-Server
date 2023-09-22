package cn.foxtech.channel.gdana.digester.service;

import cn.foxtech.channel.common.api.ChannelServerAPI;
import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.serialport.ISerialPort;
import cn.foxtech.core.exception.ServiceException;
import com.sun.jna.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ChannelService extends ChannelServerAPI {
    /**
     * 串口名-串口映射表
     */
    private final Map<String, ISerialPort> name2port = new HashMap<>();

    @Autowired
    private ExecuteService executeService;
    /**
     * 常量信息
     */
    @Autowired
    private ChannelProperties channelProperties;

    public void initService() {
        if (this.channelProperties.getInitMode().equals("local")) {
            this.initServiceByLocal();
        }
    }

    /**
     * 打开通道
     */
    @Override
    public void openChannel(String channelName, Map<String, Object> channelParam) {
        if (this.channelProperties.getInitMode().equals("local")) {
            return;
        }

        this.openSerial(channelName, channelParam);
    }

    /**
     * 关闭通道
     *
     * @param channelName
     */
    @Override
    public void closeChannel(String channelName, Map<String, Object> channelParam) {
        ISerialPort serialPort = this.name2port.get(channelName);
        if (serialPort == null) {
            return;
        }

        serialPort.close();
        this.name2port.remove(channelName);
    }

    private void openSerial(String channelName, Map<String, Object> channelParam) {
        String serialName = (String) channelParam.get("serial_name");
        Integer baudRate = (Integer) channelParam.get("baud_rate");
        Integer databits = (Integer) channelParam.get("databits");
        String parity = (String) channelParam.get("parity");
        Integer stopbits = (Integer) channelParam.get("stopbits");

        // 打开串口
        ISerialPort serialPort = ISerialPort.newInstance();
        if (!serialPort.open(serialName)) {
            return;
        }

        // 设置串口参数
        serialPort.setParam(baudRate, parity, databits, stopbits, 0);

        // 保存串口对象
        this.name2port.put(channelName, serialPort);
    }


    /**
     * 装载串口
     */
    private void initServiceByLocal() {
        try {
            // 读取JSON格式的配置文件
            File file = new File("");
            String confFileName = file.getAbsolutePath();
            if (Platform.isLinux()) {
                confFileName += "/conf/fox-edge-server-channel-gdana-digester-linux.conf";
            } else if (Platform.isWindows()) {
                confFileName += "/conf/fox-edge-server-channel-gdana-digester-win32.conf";
            }

            String jsonData = new String(Files.readAllBytes(Paths.get(confFileName)));
            Map<String, Object> jsonMap = JsonUtils.buildObject(jsonData, Map.class);
            List<Map<String, Object>> paramList = (List<Map<String, Object>>) jsonMap.get("serial_port");
            for (Map<String, Object> param : paramList) {
                String channelName = (String) param.get("name");
                this.openSerial(channelName, param);
            }
        } catch (IOException e) {
            System.out.print(e);
        }
    }


    /**
     * 查询串口数据:增加同步锁，避免并发访问带来的多线程异常。
     *
     * @return
     */
    @Override
    public synchronized ChannelRespondVO execute(ChannelRequestVO requestVO) throws ServiceException {
        ISerialPort serialPort = this.name2port.get(requestVO.getName());
        return this.executeService.execute(serialPort, requestVO);
    }

    /**
     * 执行发布操作
     *
     * @param requestVO 发布报文
     * @throws ServiceException 异常信息
     */
    @Override
    public void publish(ChannelRequestVO requestVO) throws ServiceException {
        ISerialPort serialPort = this.name2port.get(requestVO.getName());
        this.executeService.publish(serialPort, requestVO);
    }
}
