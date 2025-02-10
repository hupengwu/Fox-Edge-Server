/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 *
 *     This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * --------------------------------------------------------------------------- */
 
package cn.foxtech.channel.s7plc.client.service;

import cn.foxtech.channel.common.api.ChannelServerAPI;
import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.device.protocol.v1.s7plc.core.enums.EPlcType;
import cn.foxtech.device.protocol.v1.s7plc.core.service.S7PLC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ChannelService extends ChannelServerAPI {
    /**
     * 串口名-串口映射表
     */
    private final Map<String, S7PLC> name2entity = new HashMap<>();

    @Autowired
    private ExecuteService executeService;


    public void initService() {
    }

    /**
     * 打开通道
     *
     * @param channelName  通道名称
     * @param channelParam 通道参数
     */
    @Override
    public void openChannel(String channelName, Map<String, Object> channelParam) {
        String ip = (String) channelParam.getOrDefault("ip", "127.0.0.1");
        Integer port = (Integer) channelParam.getOrDefault("port", 102);
        String plcType = (String) channelParam.getOrDefault("plcType", EPlcType.S1200.name());
        Integer rack = (Integer) channelParam.getOrDefault("rack", 0);
        Integer slot = (Integer) channelParam.getOrDefault("slot", 1);
        Integer pduLength = (Integer) channelParam.getOrDefault("pduLength", 240);


        S7PLC entity = new S7PLC(EPlcType.valueOf(plcType), ip, port, rack, slot, pduLength);

        // 保存串口对象
        this.name2entity.put(channelName, entity);
    }

    /**
     * 关闭通道
     *
     * @param channelName  通道名称
     * @param channelParam 通道参数
     */
    @Override
    public void closeChannel(String channelName, Map<String, Object> channelParam) {
        S7PLC entity = this.name2entity.get(channelName);
        if (entity == null) {
            return;
        }

        entity.close();

        this.name2entity.remove(channelName);
    }


    /**
     * 查询串口数据:增加同步锁，避免并发访问带来的多线程异常。
     *
     * @param requestVO 请求报文
     * @return 响应报文
     * @throws ServiceException 异常
     */
    @Override
    public synchronized ChannelRespondVO execute(ChannelRequestVO requestVO) throws ServiceException {
        S7PLC entity = this.name2entity.get(requestVO.getName());
        return this.executeService.execute(entity, requestVO);
    }
}
