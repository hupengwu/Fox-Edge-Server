package cn.foxtech.channel.serialport.service;

import cn.foxtech.channel.common.api.ChannelServerAPI;
import cn.foxtech.channel.common.utils.ChannelStatusUtils;
import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.channel.domain.ChannelVOConstant;
import cn.foxtech.channel.serialport.entity.SerialChannelEntity;
import cn.foxtech.channel.serialport.entity.SerialConfigEntity;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.serialport.AsyncExecutor;
import cn.foxtech.common.utils.serialport.ISerialPort;
import cn.foxtech.core.exception.ServiceException;
import com.fasterxml.jackson.core.JsonParseException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChannelService extends ChannelServerAPI {
    private static final Logger logger = Logger.getLogger(ChannelService.class);

    private final Map<String, SerialChannelEntity> channelEntityMap = new ConcurrentHashMap<>();
    @Autowired
    private RedisConsoleService console;
    @Autowired
    private ExecuteService executeService;

    @Autowired
    private ReportService reportService;

    @Override
    public Object getReportLock() {
        return this.reportService;
    }


    /**
     * 打开通道
     *
     * @param channelName  通道名称
     * @param channelParam 通道参数
     */
    @Override
    public void openChannel(String channelName, Map<String, Object> channelParam) throws JsonParseException {
        // 创建一个实体对象
        SerialChannelEntity channelEntity = this.channelEntityMap.get(channelName);
        if (channelEntity == null) {
            channelEntity = new SerialChannelEntity();
            channelEntity.setChannelName(channelName);
            this.channelEntityMap.put(channelName, channelEntity);
        }

        // 取出配置参数
        String serialName = (String) channelParam.get("serialName");
        Integer baudRate = (Integer) channelParam.get("baudRate");
        Integer databits = (Integer) channelParam.get("databits");
        String parity = (String) channelParam.get("parity");
        Integer stopbits = (Integer) channelParam.get("stopbits");
        if (MethodUtils.hasEmpty(serialName, baudRate, databits, parity, stopbits, stopbits)) {
            throw new ServiceException("配置参数不能为空:serialName, baudRate, databits, parity, stopbits, stopbits");
        }

        // 生成配置实体
        SerialConfigEntity configEntity = new SerialConfigEntity();
        configEntity.setSerialName(serialName);
        configEntity.setBaudRate(baudRate);
        configEntity.setDatabits(databits);
        configEntity.setParity(parity);
        configEntity.setStopbits(stopbits);
        configEntity.setMinPackInterval((Integer) channelParam.getOrDefault("packInterval", 0));
        configEntity.setFullDuplex(Boolean.TRUE.equals(channelParam.get("fullDuplex")));

        // 保存配置
        channelEntity.setConfig(configEntity);

        // 打开串口
        this.openSerial(channelName);
    }

    /**
     * 打开串口
     *
     * @param channelName 通道名称
     */
    private void openSerial(String channelName) {
        SerialChannelEntity channelEntity = this.channelEntityMap.get(channelName);
        if (channelEntity == null) {
            throw new ServiceException("该channel实体对象不存在:" + channelName);
        }

        if (channelEntity.getConfig() == null) {
            throw new ServiceException("配置参数不正确:" + channelName);
        }

        // 打开串口
        if (channelEntity.getSerialPort() == null) {
            ISerialPort serialPort = ISerialPort.newInstance();

            SerialConfigEntity config = channelEntity.getConfig();

            // 打开串口
            if (!serialPort.open(config.getSerialName())) {
                throw new ServiceException("打开串口失败:" + channelName);
            }

            // 设置串口参数
            serialPort.setParam(config.getBaudRate(), config.getParity(), config.getDatabits(), config.getStopbits());

            // 记录打开的串口对象
            channelEntity.setSerialPort(serialPort);

            // 全双工模式：创建一个异步执行器
            if (channelEntity.getConfig().getFullDuplex()) {
                AsyncExecutor asyncExecutor = new AsyncExecutor();

                // 绑定一个数据到达通知的匿名接口，那么当有串口数据到达的时候，用户自定义线程可以在getReportLock()，wait()这个notify
                asyncExecutor.setReadableNotify(() -> {
                    synchronized (this.reportService) {
                        this.reportService.notify();
                    }
                });

                asyncExecutor.createExecutor(serialPort);

                channelEntity.setAsyncExecutor(asyncExecutor);
            }
        }
    }

    /**
     * 关闭通道
     *
     * @param channelName  通道名称
     * @param channelParam 通道参数
     */
    @Override
    public void closeChannel(String channelName, Map<String, Object> channelParam) {
        // 创建一个实体对象
        SerialChannelEntity channelEntity = this.channelEntityMap.get(channelName);
        if (channelEntity == null) {
            return;
        }

        //  关闭串口
        if (channelEntity.getSerialPort() != null) {
            try {
                // 关闭异步执行器
                if (channelEntity.getAsyncExecutor() != null) {
                    channelEntity.getAsyncExecutor().closeExecutor();
                    channelEntity.setAsyncExecutor(null);
                }

                // 关闭串口
                channelEntity.getSerialPort().close();
                channelEntity.setSerialPort(null);
            } catch (Exception e) {
                this.console.error(e.getMessage());
                logger.error(e.getMessage());
            }
        }

        // 删除实体
        this.channelEntityMap.remove(channelName);
    }


    @Override
    public synchronized ChannelRespondVO manageChannel(ChannelRequestVO requestVO) {
        Map<String, Object> requestParam = (Map<String, Object>) requestVO.getSend();
        if (MethodUtils.hasEmpty(requestParam)) {
            throw new ServiceException("send参数不能为空!");
        }

        String operate = (String) requestParam.get(ChannelVOConstant.filed_operate);
        if (MethodUtils.hasEmpty(operate)) {
            throw new ServiceException("operate参数不能为空!");
        }

        Object recv = null;
        if (operate.equals(ChannelVOConstant.value_operate_get_status)) {
            Map<String, Object> result = new HashMap<>();
            List<String> channelNameList = (List<String>) requestParam.get(ChannelVOConstant.filed_param);
            for (String channelName : channelNameList) {
                SerialChannelEntity channelEntity = this.channelEntityMap.get(channelName);
                if (channelEntity == null || channelEntity.getSerialPort() == null || !channelEntity.getSerialPort().isOpen()) {
                    result.put(channelName, ChannelStatusUtils.buildStatus(false, 0));
                    continue;
                }

                result.put(channelName, ChannelStatusUtils.buildStatus(true, System.currentTimeMillis()));
            }

            recv = result;
        } else {
            throw new ServiceException("不支持的operate参数!");
        }


        ChannelRespondVO respondVO = new ChannelRespondVO();
        respondVO.bindBaseVO(requestVO);
        respondVO.setRecv(recv);
        return respondVO;

    }

    /**
     * 查询串口数据:增加同步锁，避免并发访问带来的多线程异常。
     *
     * @return
     */
    @Override
    public synchronized ChannelRespondVO execute(ChannelRequestVO requestVO) throws ServiceException {
        SerialChannelEntity channelEntity = this.channelEntityMap.get(requestVO.getName());
        if (channelEntity == null) {
            throw new ServiceException("该channel不存在:" + requestVO.getName());
        }

        if (channelEntity.getConfig() == null) {
            throw new ServiceException("该channel上的配置参数不正确:" + requestVO.getName());
        }

        if (channelEntity.getSerialPort() == null || !channelEntity.getSerialPort().isOpen()) {
            throw new ServiceException("串口无法打开:" + requestVO.getName());
        }

        if (channelEntity.getConfig().getFullDuplex()) {
            throw new ServiceException("异步全双工模式，不允许进行半双工操作:" + requestVO.getName());
        }

        return this.executeService.execute(channelEntity.getSerialPort(), channelEntity.getConfig().getMinPackInterval(), requestVO);
    }

    /**
     * 执行发布操作
     *
     * @param requestVO 发布报文
     * @throws ServiceException 异常信息
     */
    @Override
    public void publish(ChannelRequestVO requestVO) throws ServiceException {
        SerialChannelEntity channelEntity = this.channelEntityMap.get(requestVO.getName());
        if (channelEntity == null) {
            throw new ServiceException("该channel不存在:" + requestVO.getName());
        }

        if (channelEntity.getSerialPort() == null) {
            throw new ServiceException("串口不存在:" + requestVO.getName());
        }
        if (!channelEntity.getSerialPort().isOpen()) {
            throw new ServiceException("串口未打开:" + requestVO.getName());
        }

        this.executeService.publish(channelEntity, requestVO);
    }

    @Override
    public List<ChannelRespondVO> report() throws ServiceException {
        return this.reportService.report(this.channelEntityMap);
    }
}
