package cn.foxtech.channel.serialport.service;

import cn.foxtech.channel.serialport.entity.SerialChannelEntity;
import cn.foxtech.channel.serialport.entity.SerialPortEntity;
import cn.foxtech.channel.serialport.entity.SerialStreamEntity;
import cn.foxtech.channel.serialport.script.ScriptServiceKey;
import cn.foxtech.channel.serialport.script.ScriptSplitMessage;
import cn.foxtech.common.entity.manager.LocalConfigService;
import cn.foxtech.common.utils.hex.HexUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.serialport.AsyncExecutor;
import cn.foxtech.common.utils.serialport.ISerialPort;
import cn.foxtech.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 启动TCP服务器的异步线程
 */
@Component
public class ServerInitializer {
    private final SerialPortEntity serialPortEntity = new SerialPortEntity();
    @Autowired
    private ChannelService channelService;
    @Autowired
    private LocalConfigService localConfigService;

    @Autowired
    private ReportService reportService;

    public void initialize() {
        // 读取配置参数
        this.localConfigService.initialize();
        Map<String, Object> configs = this.localConfigService.getConfigs();
        Map<String, Object> params = (Map<String, Object>) configs.getOrDefault("serialPort", new HashMap<>());

        // 打开串口
        this.openSerial(params);

        // 创建流量拼接线程
        this.createStreamServer();

        // 创建报文分析线程
        this.createAnalyseServer();
    }

    private void createStreamServer() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(100);

                        appendData();

                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
            }
        }, 0, TimeUnit.MILLISECONDS);
        scheduledExecutorService.shutdown();
    }

    private void createAnalyseServer() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        analyseData();

                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
            }
        }, 0, TimeUnit.MILLISECONDS);
        scheduledExecutorService.shutdown();
    }

    /**
     * 将数据追加到缓存之中
     */
    private void appendData() {
        // 检测：串口是否打开
        if (this.serialPortEntity.getSerialPort() == null) {
            return;
        }

        if (!this.serialPortEntity.getSerialPort().isOpen()) {
            return;
        }

        // 取得异步执行器
        AsyncExecutor asyncExecutor = this.serialPortEntity.getAsyncExecutor();
        if (asyncExecutor == null) {
            return;
        }

        // 检测：是否有数据到达
        if (!asyncExecutor.isReadable()) {
            return;
        }

        SerialStreamEntity streamEntity = this.serialPortEntity.getStreamEntity();

        // 取出数据
        List<byte[]> list = asyncExecutor.waitReadable(100);

        for (byte[] data : list) {
            // 检查：是否会溢出，如果要发生溢出，说明这个数据可能已经异常了，没有人能处理了，直接废弃吧
            if (SerialStreamEntity.max < streamEntity.getEnd() + data.length) {
                streamEntity.clear();
                continue;
            }

            synchronized (streamEntity) {

                // 复制数据到缓存之中，等待JSP引擎进行遍历性的解析
                streamEntity.addTail(data);

                // 发出notify
                streamEntity.notify();
            }
        }
    }

    private void analyseData() {
        try {
            SerialStreamEntity streamEntity = this.serialPortEntity.getStreamEntity();
            synchronized (streamEntity) {
                // 等待消息别的线程的notify
                streamEntity.wait(10 * 1000);

                if (streamEntity.getEnd() == 0) {
                    return;
                }

                Map<String, SerialChannelEntity> channelEntityMap = this.channelService.getChannelEntityMap();
                for (String channelName : channelEntityMap.keySet()) {
                    SerialChannelEntity channelEntity = channelEntityMap.get(channelName);

                    // 取出脚本引擎
                    ScriptSplitMessage splitScript = channelEntity.getSplitScript();
                    ScriptServiceKey keyScript = channelEntity.getKeyScript();
                    if (splitScript == null || keyScript == null) {
                        continue;
                    }


                    // 粘包的情况
                    while (true) {
                        try {
                            // 分拆报文
                            byte[] data = this.decode(splitScript, streamEntity);
                            if (data == null) {
                                break;
                            }

                            // 提取业务特征
                            String key = this.decode(keyScript, data);
                            if (key.isEmpty()) {
                                continue;
                            }

                            // 比较业务特征
                            if (!key.equals(channelEntity.getChannelParam().get("serviceKey"))) {
                                continue;
                            }

                            // 返回格式：是否进行文本转码
                            String returnText = (String) channelEntity.getChannelParam().get("returnText");
                            if (!MethodUtils.hasEmpty(returnText)) {
                                String message = new String(data, returnText);
                                this.reportService.insert(channelName, message);
                            } else {
                                this.reportService.insert(channelName, data);
                            }
                        } catch (Exception e) {
                            break;
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private byte[] decode(ScriptSplitMessage splitMessage, SerialStreamEntity streamEntity) {
        String res = splitMessage.decode(streamEntity.getBuff(), streamEntity.getEnd());
        if (res.isEmpty()) {
            return null;
        }

        // 转换数据格式
        byte[] data = HexUtils.hexStringToByteArray(res);

        // 移走数据
        streamEntity.movHead(data);

        return data;
    }

    private String decode(ScriptServiceKey scriptServiceKey, byte[] data) throws UnsupportedEncodingException {
        String res = scriptServiceKey.decode(data);
        if (res.isEmpty()) {
            return "";
        }
        return res;
    }

    public void openSerial(Map<String, Object> channelParam) {
        // 取出配置参数
        String serialName = (String) channelParam.get("serialName");
        Integer baudRate = (Integer) channelParam.get("baudRate");
        Integer databits = (Integer) channelParam.get("databits");
        String parity = (String) channelParam.get("parity");
        Integer stopbits = (Integer) channelParam.get("stopbits");
        if (MethodUtils.hasEmpty(serialName, baudRate, databits, parity, stopbits, stopbits)) {
            throw new ServiceException("配置参数不能为空:serialName, baudRate, databits, parity, stopbits, stopbits");
        }


        ISerialPort serialPort = ISerialPort.newInstance();

        // 打开串口
        if (!serialPort.open(serialName)) {
            throw new ServiceException("打开串口失败:" + serialName);
        }

        // 记录打开的串口对象
        this.serialPortEntity.setSerialPort(serialPort);

        // 设置串口参数
        serialPort.setParam(baudRate, parity, databits, stopbits);


        // 全双工模式：创建一个异步执行器
        AsyncExecutor asyncExecutor = new AsyncExecutor();
        asyncExecutor.createExecutor(serialPort);

        this.serialPortEntity.setAsyncExecutor(asyncExecutor);

    }
}