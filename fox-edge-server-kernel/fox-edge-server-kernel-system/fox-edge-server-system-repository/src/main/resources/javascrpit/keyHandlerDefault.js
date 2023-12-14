/**
 * 函数说明：当前报文携带的设备身份ID
 * 输入参数：HEX格式的字符串，或者是TXT格式的字符串，（具体看通道服务的配置，给你输入的是哪种格式，默认是HEX格式），用于表达接收到的一个报文
 * 返回格式：当前设备身份信息的字符串
 * 备注信息：在TCP上传输的协议，它的报文会包身份信息，用于帮助识别是哪个设备主动送过来的数据。
 */
function getServiceKey(message)
{
    // LORA WAN的报文，它实际上没有设备ID，此时使用LoRa Wan Device来标识
    if(message.length() < 32){
        return "LoRa Wan Device";
    }

    var deviceId = message.substring(8,23);
    return  deviceId;
}