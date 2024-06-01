package cn.foxtech.common.utils.md5;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
 * MD5，Message Digest Algorithm 5，是一种被广泛使用的信息摘要算法，
 * 可以将给定的任意长度数据通过一定的算法计算得出一个 128 位固定长度的散列值
 */
public class MD5Utils {
    public static String getMD5Txt(String data) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return MD5Utils.getMD5(data.getBytes("UTF-8")).toString(16).toUpperCase();
    }

    /**
     * 计算MD5数值
     *
     * @param data 待计算的数据
     * @return MD5数值
     * @throws NoSuchAlgorithmException JAVA库环境的异常
     */
    public static BigInteger getMD5(String data) throws NoSuchAlgorithmException {
        return MD5Utils.getMD5(data.getBytes());
    }

    /**
     * 计算MD5数值
     *
     * @param data 待计算的数据
     * @return MD5数值
     * @throws NoSuchAlgorithmException JAVA库环境的异常
     */
    public static BigInteger getMD5(byte[] data) throws NoSuchAlgorithmException {
        // 第一步，获取MessageDigest对象，参数为MD5字符串，表示这是一个MD5算法
        MessageDigest md5 = MessageDigest.getInstance("MD5");

        // 重置
        md5.reset();

        // 第二步，输入源数据，参数类型为byte[]
        md5.update(data);

        // 第三步，计算MD5值
        // String resultArray = md5.digest().toString();
        /*
         * digest() 方法返回值是一个字节数组类型的 16 位长度的哈希值，通常，我们会
         * 转化为十六进制的 32 位长度的字符串来使用，可以利用 BigInteger 类来做这个转化：
         */
        BigInteger bigInt = new BigInteger(1, md5.digest());

        // 重置
        md5.reset();
        
        return bigInt;
    }

    public static String getMD5Txt(File file) {
        try {
            byte[] md5 = MD5Utils.getMD5(file);
            BigInteger bigInt = new BigInteger(1, md5);
            return bigInt.toString(16).toUpperCase();
        } catch (Exception e) {
            return "";
        }
    }

    public static byte[] getMD5(File file) throws IOException, NoSuchAlgorithmException {
        InputStream in = null;

        try {
            in = new FileInputStream(file);
            return getMD5(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    private static byte[] getMD5(InputStream data) throws IOException, NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");

        // 重置
        md5.reset();

        byte[] buffer = new byte[8192];

        int read;
        while ((read = data.read(buffer, 0, 8192)) > -1) {
            md5.update(buffer, 0, read);
        }

        byte[] result = md5.digest();

        // 重置
        md5.reset();

        return result;
    }
}
