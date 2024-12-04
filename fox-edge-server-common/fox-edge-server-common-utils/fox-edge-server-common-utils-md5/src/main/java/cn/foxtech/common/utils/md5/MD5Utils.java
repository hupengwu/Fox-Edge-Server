/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.utils.md5;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/*
 * MD5，Message Digest Algorithm 5，是一种被广泛使用的信息摘要算法，
 * 可以将给定的任意长度数据通过一定的算法计算得出一个 128 位固定长度的散列值
 */
public class MD5Utils {
    private static final byte[] block = new byte[1024];

    private static final int blockSize = 1024;

    public static String getMD5Txt(String data) throws NoSuchAlgorithmException {
        byte[] md5 = MD5Utils.getMD5Text(data.getBytes(StandardCharsets.UTF_8));
        BigInteger bigInt = new BigInteger(1, md5);
        return bigInt.toString(16).toUpperCase();
    }

    /**
     * 计算MD5数值
     *
     * @param data 待计算的数据
     * @return MD5数值
     * @throws NoSuchAlgorithmException JAVA库环境的异常
     */
    private static synchronized byte[] getMD5Text(byte[] data) throws NoSuchAlgorithmException {
        // 第一步，获取MessageDigest对象，参数为MD5字符串，表示这是一个MD5算法
        MessageDigest md5 = MessageDigest.getInstance("MD5");

        // 重置
        md5.reset();

        int number = data.length / blockSize;
        int remainder = data.length % blockSize;

        for (int i = 0; i < number; i++) {
            md5.update(data, blockSize * i, blockSize);
        }
        if (remainder > 0) {
            md5.update(data, blockSize * number, remainder);
        }

        byte[] result = md5.digest();

        // 重置
        md5.reset();

        return result;
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

    private static synchronized byte[] getMD5(InputStream data) throws IOException, NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");

        // 重置
        md5.reset();

        int read;
        while ((read = data.read(block, 0, 1024)) > -1) {
            md5.update(block, 0, read);
        }

        byte[] result = md5.digest();

        // 重置
        md5.reset();

        return result;
    }

    public static String getMapMD5Text(Map data) throws NoSuchAlgorithmException {
        String str = getMapString(data);

        str = MD5Utils.getMD5Txt(str);
        return str;
    }

    public static String getListMD5Text(List data) throws NoSuchAlgorithmException {
        String str = getListString(data);

        str = MD5Utils.getMD5Txt(str);
        return str;
    }

    public static String getMapString(Map data) {
        List keys = new ArrayList<>();
        keys.addAll(data.keySet());

        Collections.sort(keys);

        StringBuilder str = new StringBuilder();
        str.append("[");
        for (Object key : keys) {
            Object value = data.get(key);

            str.append(key);
            str.append("=");

            if (value instanceof Map) {
                str.append(getMapString((Map) value));
            } else if (value instanceof List) {
                str.append(getListString((List) value));
            } else {
                str.append(value);
            }

            str.append(",");
        }

        if (str.length() > 1) {
            str.deleteCharAt(str.length() - 1);
        }
        str.append("]");

        return str.toString();
    }

    public static String getListString(List data) {
        Collections.sort(data);

        StringBuilder str = new StringBuilder();
        for (Object value : data) {
            if (value instanceof Map) {
                str.append(getMapString((Map) value));
            } else if (value instanceof List) {
                str.append(getListString((List) value));
            } else {
                str.append(value);
            }

            str.append(",");
        }

        if (str.length() > 1) {
            str.deleteCharAt(str.length() - 1);
        }

        return str.toString();
    }
}
