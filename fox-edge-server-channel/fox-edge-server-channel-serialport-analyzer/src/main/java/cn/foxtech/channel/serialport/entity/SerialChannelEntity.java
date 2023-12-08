package cn.foxtech.channel.serialport.entity;

import cn.foxtech.channel.serialport.script.ScriptServiceKey;
import cn.foxtech.channel.serialport.script.ScriptSplitMessage;
import cn.foxtech.common.entity.entity.OperateEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class SerialChannelEntity {
    private Map<String, Object> channelParam = new HashMap<>();

    private ScriptSplitMessage splitScript;
    private ScriptServiceKey keyScript;

    private OperateEntity splitOperate;
    private OperateEntity keyOperate;

}
