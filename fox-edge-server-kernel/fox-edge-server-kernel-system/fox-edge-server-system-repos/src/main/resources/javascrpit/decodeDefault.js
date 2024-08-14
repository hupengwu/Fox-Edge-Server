/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2020. All rights reserved.
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

/**
 * 编码器的入口函数
 * 全局参数：
 *     fox_edge_data：json string格式 或者 hex string格式的接收数据
 *     fox_edge_param：json string格式的设备参数的合并对象
 * 返回值：
 *   提供给通道的发送数据。根据不同的通道服务，它可能是HEX结构的文本，也可能是JSON结构的对象，请自行根据选定的通道服务进行确认
 */
function decode() {
    return decodeJson(fox_edge_data, fox_edge_param);
}

/**
 * 开发者自己写的解码函数1
 * 返回格式：JSON格式的字符串
 */
function decodeJson(recv, param) {
    var object = JSON.parse(param);
    return JSON.stringify(object);
}