package cn.foxtech.channel.serialport.service;

import cn.foxtech.channel.serialport.entity.SerialChannelEntity;
import cn.foxtech.channel.serialport.entity.SerialPortEntity;
import cn.foxtech.channel.serialport.entity.SerialStreamEntity;
import cn.foxtech.channel.serialport.script.ScriptServiceKey;
import cn.foxtech.channel.serialport.script.ScriptSplitMessage;
import cn.foxtech.common.entity.manager.LocalConfigService;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.hex.HexUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.serialport.AsyncExecutor;
import cn.foxtech.common.utils.serialport.ISerialPort;
import cn.foxtech.core.exception.ServiceException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private final Logger logger = Logger.getLogger(ServerInitializer.class);

    private final SerialPortEntity serialPortEntity = new SerialPortEntity();
    @Autowired
    private ChannelService channelService;
    @Autowired
    private LocalConfigService localConfigService;

    @Autowired
    private ReportService reportService;


    @Autowired
    private RedisConsoleService console;


    /**
     * 是否记录日志
     */
    private boolean openLogger;

    public void initialize() {
        // 读取配置参数
        this.localConfigService.initialize();
        Map<String, Object> configs = this.localConfigService.getConfig();
        Map<String, Object> params = (Map<String, Object>) configs.getOrDefault("serialPort", new HashMap<>());
        this.openLogger = (Boolean) configs.getOrDefault("logger", false);


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
                        if (openLogger) {
                            console.error("接收数据异常：" + e.getMessage());
                        }
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
                        if (openLogger) {
                            console.error("分析数据异常：" + e.getMessage());
                        }
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

                if (this.openLogger) {
                    this.console.info("串口接收到数据:" + HexUtils.byteArrayToHexString(data));
                }
            }
        }
    }

    private void analyseData() {
        try {
            SerialStreamEntity streamEntity = this.serialPortEntity.getStreamEntity();
            synchronized (streamEntity) {
                // 等待消息别的线程的notify
                streamEntity.wait(1000);

                if (streamEntity.getEnd() == 0) {
                    return;
                }

                Map<String, SerialChannelEntity> channelEntityMap = this.channelService.getChannelEntityMap();
                for (String channelName : channelEntityMap.keySet()) {
                    SerialChannelEntity channelEntity = channelEntityMap.get(channelName);

                    // 取出脚本引擎
                    ScriptSplitMessage splitScript = channelEntity.getSplitScript();
                    if (splitScript == null) {
                        continue;
                    }


                    // 粘包的情况
                    while (true) {
                        try {
                            // 必选条件：分拆报文
                            byte[] data = this.decode(splitScript, streamEntity);
                            if (data == null) {
                                break;
                            }

                            // 可选条件：如果用户要求按业务特征进行细分，那么就再进行业务特征的识别
                            if (!this.checkKey(channelEntity, data)) {
                                continue;
                            }

                            // 返回格式：是否进行文本转码
                            String returnText = (String) channelEntity.getChannelParam().get("returnText");
                            if (!MethodUtils.hasEmpty(returnText)) {
                                String message = new String(data, returnText);
                                this.reportService.push(channelName, message);

                                if (this.openLogger) {
                                    this.console.info("上报报文:" + message);
                                }

                            } else {
                                this.reportService.push(channelName, data);

                                if (this.openLogger) {
                                    this.console.info("串口接收到数据:" + HexUtils.byteArrayToHexString(data));
                                }
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

    private boolean checkKey(SerialChannelEntity channelEntity, byte[] data) {
        // 可选条件：如果用户没有配置，那么不进行检查，直接返回成功
        ScriptServiceKey keyScript = channelEntity.getKeyScript();
        if (keyScript == null) {
            return true;
        }

        // 提取业务特征
        String key = keyScript.decode(data);
        if (key.isEmpty()) {
            return false;
        }

        // 比较业务特征
        return key.equals(channelEntity.getChannelParam().get("serviceKey"));
    }

    private byte[] decode(ScriptSplitMessage splitMessage, SerialStreamEntity streamEntity) {
        String res = splitMessage.decode(streamEntity.getBuff(), streamEntity.getEnd());
        if (res.isEmpty()) {
            return null;
        }

        if (this.openLogger) {
            this.console.info("分拆出了报文:" + res);
        }

        // 转换数据格式
        byte[] data = HexUtils.hexStringToByteArray(res);

        // 移走数据
        streamEntity.movHead(data);

        return data;
    }

    public void openSerial(Map<String, Object> channelParam) {
        try {
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
        } catch (Exception e) {
            String message = "初始化串口出错:" + e.getMessage();
            this.logger.error(message);
            this.console.error(message);
        }

    }
}
