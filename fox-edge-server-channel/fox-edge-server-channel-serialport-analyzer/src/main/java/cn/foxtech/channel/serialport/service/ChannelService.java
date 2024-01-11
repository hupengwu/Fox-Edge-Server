package cn.foxtech.channel.serialport.service;

import cn.foxtech.channel.common.api.ChannelServerAPI;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.channel.serialport.entity.SerialChannelEntity;
import cn.foxtech.channel.serialport.script.ScriptEngineService;
import cn.foxtech.channel.serialport.script.ScriptServiceKey;
import cn.foxtech.channel.serialport.script.ScriptSplitMessage;
import cn.foxtech.common.entity.entity.OperateEntity;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.exception.ServiceException;
import com.fasterxml.jackson.core.JsonParseException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.script.ScriptException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChannelService extends ChannelServerAPI {
    @Getter
    private final Map<String, SerialChannelEntity> channelEntityMap = new ConcurrentHashMap<>();

    @Autowired
    private ReportService reportService;


    @Autowired
    private ScriptEngineService scriptEngineService;

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
    public void openChannel(String channelName, Map<String, Object> channelParam) throws JsonParseException, ScriptException, NoSuchMethodException {
        String manufacturer = (String) channelParam.get("manufacturer");
        String deviceType = (String) channelParam.get("deviceType");
        String splitHandler = (String) channelParam.get("splitHandler");

        // 必选项
        if (MethodUtils.hasEmpty(manufacturer, deviceType, splitHandler)) {
            throw new ServiceException("参数不能为空: manufacturer, deviceType, splitHandler");
        }


        // 构造脚本引擎的绑定关系
        SerialChannelEntity channelEntity = new SerialChannelEntity();
        this.rebindScriptEngine(channelEntity, channelParam);

        this.channelEntityMap.put(channelName, channelEntity);


    }

    public void rebindScriptEngine(SerialChannelEntity channelEntity, Map<String, Object> channelParam) throws ScriptException, NoSuchMethodException {
        channelEntity.getChannelParam().putAll(channelParam);

        // 获得操作实体:splitOperate，必选项目
        OperateEntity splitOperate = this.scriptEngineService.getSplitOperate(channelParam);
        ScriptSplitMessage splitScript = this.scriptEngineService.buildSplitOperate(splitOperate);
        channelEntity.setSplitScript(splitScript);
        channelEntity.setSplitOperate(splitOperate);

        // 获得操作实体:keyHandler，可选项目
        String keyHandler = (String) channelParam.get("keyHandler");
        if (!MethodUtils.hasEmpty(keyHandler)) {
            OperateEntity keyOperate = this.scriptEngineService.getKeyOperate(channelParam);
            ScriptServiceKey keyScript = this.scriptEngineService.buildServiceKeyOperate(keyOperate);
            channelEntity.setKeyScript(keyScript);
            channelEntity.setKeyOperate(keyOperate);
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
        this.channelEntityMap.remove(channelName);
    }

    /**
     * 主动上报操作：单向上行
     *
     * @return 上行报文
     * @throws ServiceException 异常信息
     */
    @Override
    public synchronized List<ChannelRespondVO> report() throws ServiceException {
        return this.reportService.report();
    }

}
