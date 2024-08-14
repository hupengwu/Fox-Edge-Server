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
 
package cn.foxtech.common.rpc.redis.channel.client;

import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.utils.redis.value.RedisValueService;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 记录类型的队列：这是可靠性记录，它会在redis之中缓存
 */
@Component
public class RedisListChannelClientRespond extends RedisValueService {
    @Getter
    private final String key = "fox.edge.list:channel:";

    public ChannelRespondVO get(String channelType, String hashKey, long timeout) {
        try {
            Object map = super.get(this.key + channelType + ":respond", hashKey, timeout);
            if (map == null) {
                return null;
            }

            return ChannelRespondVO.buildVO((Map<String, Object>) map);
        } catch (Exception e) {
            return null;
        }
    }
}