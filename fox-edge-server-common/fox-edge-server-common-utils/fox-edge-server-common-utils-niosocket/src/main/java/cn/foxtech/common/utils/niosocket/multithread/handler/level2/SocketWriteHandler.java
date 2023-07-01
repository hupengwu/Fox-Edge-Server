package cn.foxtech.common.utils.niosocket.multithread.handler.level2;


import cn.foxtech.common.utils.niosocket.multithread.handler.ISocketL2Handler;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;

/**
 * 可以写数据的消息响应：发送数据
  * 处理Write消息：当可以给客户端发送数据的时候，会捕获到这个动作
  * 对象：NioServerSocket/NioClientSocket
  *
 * @author h00442047
 * @since 2019年12月19日
 */
public class SocketWriteHandler implements ISocketL2Handler {
    /**
     * 处理进入消息：
     * 写线程中绑定的附件是Queue结构的对象，里面保存待发送的数据
     *
     * @param key key
     */
    public void handle(SelectionKey key) {
        try {
            // 取出channel
            SocketChannel sc = (SocketChannel) key.channel();

            // 取出附件:缓存
            @SuppressWarnings("unchecked")
            Queue<byte[]> queue = (Queue<byte[]>) key.attachment();

            // 计算待发送的数据总量
            int sum = 0;
            int size = queue.size();
            Iterator<byte[]> it = queue.iterator();
            for (int i = 0; i < size; i++) {
                if (it.hasNext()) {
                    byte[] dat = it.next();
                    sum += dat.length;
                }
            }
            // 发送数据
            if (sum > 0) {
                ByteBuffer buff = ByteBuffer.allocate(sum);
                while (!queue.isEmpty()) {
                    buff.put(queue.poll());
                }
                buff.flip();

                sc.write(buff);

                buff.clear();
            }
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
