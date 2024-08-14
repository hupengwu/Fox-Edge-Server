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
 
package cn.foxtech.device.protocol.v1.core.method;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class FoxEdgeMethodTemplate {
    private static final FoxEdgeMethodTemplate template = new FoxEdgeMethodTemplate();

    private Map<String, Object> exchangeMethod = new HashMap<>();

    private Map<String, Object> reportMethod = new HashMap<>();

    private Map<String, Object> publishMethod = new HashMap<>();

    public static FoxEdgeMethodTemplate inst() {
        return template;
    }

    public Map<String, Object> getExchangeMethod(String manufacturer, String deviceType) {
        Map<String, Object> deviceTypeMap = (Map<String, Object>) this.exchangeMethod.get(manufacturer);
        if (deviceTypeMap == null) {
            return new HashMap<>();
        }

        return (Map<String, Object>) deviceTypeMap.getOrDefault(deviceType, new HashMap<>());
    }

    public Map<String, Object> getPublishMethod(String manufacturer, String deviceType) {
        Map<String, Object> deviceTypeMap = (Map<String, Object>) this.publishMethod.get(manufacturer);
        if (deviceTypeMap == null) {
            return new HashMap<>();
        }

        return (Map<String, Object>) deviceTypeMap.getOrDefault(deviceType, new HashMap<>());
    }

    public Map<String, Object> getReportMethod(String manufacturer, String deviceType) {
        Map<String, Object> deviceTypeMap = (Map<String, Object>) this.reportMethod.get(manufacturer);
        if (deviceTypeMap == null) {
            return new HashMap<>();
        }

        return (Map<String, Object>) deviceTypeMap.getOrDefault(deviceType, new HashMap<>());
    }

}
