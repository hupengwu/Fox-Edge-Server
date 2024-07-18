/**
 * 编码器的入口函数
 * 全局参数：
 *     fox_edge_param：json string格式的设备参数的合并对象
 * 返回值：
 *   提供给通道的发送数据。根据不同的通道服务，它可能是HEX结构的文本，也可能是JSON结构的对象，请自行根据选定的通道服务进行确认
 */
function encode() {
    return encodeHex(fox_edge_param);
}

/**
 * 开发者自己写的编码函数1
 * 返回格式1：HEX格式的字符串
 *     比如串口通道，TCP通道、模拟通道，它们的输入/输出格式，就是HEX格式的文本
 */
function encodeHex(param) {
    return "46 49 4E 53 00 00 00 1A 00 00 00 02 00 00 00 00 80 00 02 00 0A 00 00 71 00 FF 01 01 B1 00 0A 00 00 01";
}

/**
 * 开发者自己写的编码函数2
 * 返回格式2：JSON格式的字符串
 *     比如S7通道、MQTT通道，它们的输入/输出格式，就是JSON格式的文本
 */
function encodeJson(param) {
    var object = JSON.parse(param);
    return JSON.stringify(object);
}