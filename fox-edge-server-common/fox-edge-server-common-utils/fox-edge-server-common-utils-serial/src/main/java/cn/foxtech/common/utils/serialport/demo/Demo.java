package cn.foxtech.common.utils.serialport.demo;

public class Demo {
//    public static void main(String[] args) throws InterruptedException {
//        asyncSend();
//    }
//
//    public static void asyncSend() throws InterruptedException {
//        ISerialPort serialPort = ISerialPort.newInstance();
//        serialPort.open("COM1");
//        serialPort.setParam(9600, "N", 8, 1, 0);
//
//        AsyncExecutor asyncExecutor = new AsyncExecutor();
//
//
//        byte[] send = HexUtils.hexStringToByteArray("68 11 11 11 53 12 35 68 01 02 43 C3 A6 16");
//
//        asyncExecutor.createExecutor(serialPort);
//        Thread.sleep(1000);
//        //     asyncExecutor.closeExecutor();
//
//        while (true) {
//            try {
//                // 发送数据
//                asyncExecutor.waitWriteable(send);
//                String txt = HexUtils.byteArrayToHexString(send).toUpperCase();
//                System.out.println("send->" + txt);
//
//                // 接收数据
//                List<byte[]> dataList = asyncExecutor.waitReadable(3 * 1000);
//                for (byte[] data : dataList) {
//                    txt = HexUtils.byteArrayToHexString(data).toUpperCase();
//                    System.out.println("recv<-" + txt);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public static void send() {
//        ISerialPort serialPort = ISerialPort.newInstance();
//        serialPort.open("COM1");
//        serialPort.setParam(9600, "N", 8, 1, 0);
//
//
//        byte[] send = HexUtils.hexStringToByteArray("68 11 11 11 53 12 35 68 01 02 43 C3 A6 16");
//        serialPort.sendData(send);
//
//        byte[] recv = new byte[4096];
//
//        List<byte[]> list = new LinkedList<>();
//        while (true) {
//            try {
//                int count = serialPort.sendData(send);
//                Thread.sleep(100);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//
//    }
//
//    public static void recv() {
//        ISerialPort serialPort = ISerialPort.newInstance();
//        serialPort.open("COM1");
//        serialPort.setParam(9600, "N", 8, 1, 0);
//
//
//        byte[] send = HexUtils.hexStringToByteArray("68 11 11 11 53 12 35 68 01 02 43 C3 A6 16");
//        serialPort.sendData(send);
//
//        byte[] recv = new byte[4096];
//
//        List<byte[]> list = new LinkedList<>();
//        while (true) {
//            try {
//                int count = serialPort.recvData(recv, 10, 3 * 1000);
//                if (count == 0) {
//                    continue;
//                }
//
//
//                byte[] data = new byte[count];
//                System.arraycopy(recv, 0, data, 0, count);
//                String txt = HexUtils.byteArrayToHexString(data).toUpperCase();
//                System.out.println(txt);
//                txt = "";
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public static void createServer(ISerialPort serialPort, List<byte[]> sendList) {
//        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
//        scheduledExecutorService.schedule(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    while (true) {
//                        //   send(serialPort,);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }, 0, TimeUnit.MILLISECONDS);
//        scheduledExecutorService.shutdown();
//    }
}
