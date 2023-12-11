package cn.foxtech.common.utils.serialport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 异步执行器：用于全双工模式的数据收发
 */
public class AsyncExecutor {
    /**
     * 发送缓存
     */
    private final List<byte[]> sendList = new CopyOnWriteArrayList<>();
    /**
     * 接收缓存
     */
    private final List<byte[]> recvList = new CopyOnWriteArrayList<>();
    /**
     * 线程退出标记
     */
    private final AtomicInteger exit = new AtomicInteger(0);

    /**
     * 关闭线程池
     */
    public void closeExecutor() {
        // 发出退出标记：1
        this.exit.incrementAndGet();

        while (true) {
            try {
                // 等待退出标记：发出信号，两个线程结束信号，总共3个信号
                if (this.exit.get() >= 3) {
                    return;
                }
                Thread.sleep(10);
            } catch (Exception e) {
            }
        }
    }

    /**
     * 创建线程池
     *
     * @param serialPort
     */
    public void createExecutor(ISerialPort serialPort) {
        this.createSendExecutor(serialPort);
        this.createRecvExecutor(serialPort);
    }

    /**
     * 异步发送数据
     *
     * @param data 等待发送的数据
     */
    public synchronized void waitWriteable(byte[] data) {
        synchronized (this.sendList) {
            this.sendList.add(data);
            this.sendList.notify();
        }
    }

    /**
     * 是否有接收数据到达
     *
     * @return
     */
    public synchronized boolean isReadable() {
        return !this.recvList.isEmpty();
    }

    /**
     * 异步接收数据
     *
     * @param timeout 等待超时
     * @return 接收到的一批数据
     */
    public synchronized List<byte[]> waitReadable(long timeout) {
        try {
            synchronized (this.recvList) {
                this.recvList.wait(timeout);

                List<byte[]> result = new ArrayList<>();
                if (this.recvList.isEmpty()) {
                    return result;
                }

                // 弹出一个或者全部数据
                result.addAll(this.recvList);
                this.recvList.clear();

                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    private void createSendExecutor(ISerialPort serialPort) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (exit.get() > 0) {
                            exit.incrementAndGet();
                            return;
                        }

                        List<byte[]> sendList = getSendList(3 * 1000);
                        if (sendList.isEmpty()) {
                            continue;
                        }

                        for (byte[] send : sendList) {
                            serialPort.sendData(send);
                            Thread.sleep(10);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, TimeUnit.MILLISECONDS);
        scheduledExecutorService.shutdown();
    }

    private List<byte[]> getSendList(int timeout) throws InterruptedException {
        synchronized (this.sendList) {
            // 等待消息别的线程的notify
            this.sendList.wait(timeout);

            List<byte[]> result = new ArrayList<>();
            if (this.sendList.isEmpty()) {
                return result;
            }

            // 弹出一个或者全部数据
            result.addAll(this.sendList);
            this.sendList.clear();

            return result;
        }
    }

    private void createRecvExecutor(ISerialPort serialPort) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] recv = new byte[4096];

                    while (true) {
                        if (exit.get() > 0) {
                            exit.incrementAndGet();
                            return;
                        }

                        Thread.sleep(100);

                        // 读取串口数据
                        int count = serialPort.readData(recv);
                        if (count <= 0) {
                            continue;
                        }

                        // 复制数据
                        byte[] data = new byte[count];
                        System.arraycopy(recv, 0, data, 0, count);

                        synchronized (recvList) {
                            // 保存数据
                            recvList.add(data);

                            // 通知外部有数据到达
                            recvList.notify();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, TimeUnit.MILLISECONDS);
        scheduledExecutorService.shutdown();
    }

}
