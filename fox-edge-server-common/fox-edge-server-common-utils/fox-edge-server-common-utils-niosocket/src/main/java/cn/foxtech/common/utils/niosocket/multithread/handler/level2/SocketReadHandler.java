package cn.foxtech.common.utils.niosocket.multithread.handler.level2;


import cn.foxtech.common.utils.niosocket.multithread.handler.ISocketL2Handler;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * 接收数据的响应
 * 处理Read消息：当接收到客户端发送过来的数据时，会捕获到这个动作
  * 对象：NioServerSocket/NioClientSocket
 *
 * @author h00442047
 * @since 2019年12月19日
 */
public class SocketReadHandler implements ISocketL2Handler {
    /**
     * 处理进入消息：
     * 读线程中绑定的附件是ByteBuffer结构的对象，里面保存待提取的数据
     *
     * @param key key
     */
    public void handle(SelectionKey key) {
        try {
            // 去除channel
            if (!SocketChannel.class.isInstance(key.channel())) {
                return;
            }

            // 取出附件:缓存
            if (!ByteBuffer.class.isInstance(key.attachment())) {
                return;
            }
            ByteBuffer buff = ByteBuffer.class.cast(key.attachment());

            // 处理方法1：将数据写入到一个byte数组
            int len = buff.limit() - buff.position();
            byte[] dst = new byte[len];
            buff.get(dst);

        } catch (Exception e) {
            e.toString();
        }
    }

    /**
     * 处理异常消息
     *
     * @param key key
     */
    public void handleException(SelectionKey key, Exception e) {
    }
}
