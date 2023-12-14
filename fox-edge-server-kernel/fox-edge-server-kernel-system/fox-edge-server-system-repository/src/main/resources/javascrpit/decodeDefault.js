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