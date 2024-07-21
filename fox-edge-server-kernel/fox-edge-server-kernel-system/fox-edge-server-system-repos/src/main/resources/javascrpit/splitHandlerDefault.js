/**
 * 函数说明：一组报文的最小长度
 * 返回格式：最小包长度
 * 备注信息：在TCP上传输的协议，它的报文会包括包头和包长度，用来帮助数据的分拆。
 *           包长度所在的位置通常是固定的，所以可以知道一段数据包括包头域和包长度域，至少包括多少字节。
 */
function getHeaderLength()
{
    return 8;
}


/**
 * 函数说明：当前报文的具体长度
 * 输入参数：HEX格式的字符串，或者是TXT格式的字符串，（具体看通道服务的配置，给你输入的是哪种格式，默认是HEX格式），用于表达接收到的一个报文
 * 返回格式：当前报文的长度
 * 备注信息：在TCP上传输的协议，它的报文会包括包头和包长度，用来帮助数据的分拆。
 *           包长度所在的位置通常是固定的，所以可以知道一段数据包括包头域和包长度域，至少包括多少字节。
 */
function getPackLength(message)
{
    var hex = message.substring(6,8);
    return  parseInt(hex, 16) + 8;
}

/**
 * 函数说明：当前报文是否为非法包
 * 输入参数：HEX格式的字符串，或者是TXT格式的字符串，（具体看通道服务的配置，给你输入的是哪种格式，默认是HEX格式），用于表达接收到的一个报文
 * 返回格式：布尔值
 * 备注信息：在TCP上传输的协议，它的报文会包括包头和包长度，用来帮助数据的分拆。
 *           包头通常是固定的标识，如果不是这些标识，那么说明是非法包
 */
function isInvalidPack(message)
{
    if (message.substring(0, 4) == '2424') {
        return false;
    }

    return true;
}
