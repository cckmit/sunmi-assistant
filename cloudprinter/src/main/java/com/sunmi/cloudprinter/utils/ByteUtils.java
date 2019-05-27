package com.sunmi.cloudprinter.utils;

import java.util.Arrays;

public class ByteUtils {

    public static byte[] String2Byte64(String message) {
        int len = message.length();
        byte[] bytes = new byte[len];
        char[] chars = message.toCharArray();
        byte[] byte64 = new byte[64];
        Arrays.fill(byte64, (byte) 0);
        for (int i = 0; i < len; i++) {
            bytes[i] = (byte) chars[i];
        }
        System.arraycopy(bytes, 0, byte64, 64 - bytes.length, bytes.length);
        return byte64;
    }

    public static byte[] getNoneByte64() {
        byte[] byte64 = new byte[64];
        Arrays.fill(byte64, (byte) 0);
        return byte64;
    }

    /**
     * int整数转换为4字节的byte数组
     *
     * @param i
     * @return byte
     */
    public static byte[] intToByte2(int i) {
        byte[] targets = new byte[4];
        targets[1] = (byte) (i & 0xFF);
        targets[0] = (byte) (i >> 8 & 0xFF);
        return targets;
    }

    public static int byte2ToInt(byte[] bytes) {
        int b0 = bytes[0] & 0xFF;
        int b1 = bytes[1] & 0xFF;
        return (b1 << 8) | b0;
    }

    /**
     * int整数转换为4字节的byte数组
     *
     * @param i
     * @return byte
     */
    public static byte[] intToByte4(int i) {
        byte[] targets = new byte[4];
        targets[3] = (byte) (i & 0xFF);
        targets[2] = (byte) (i >> 8 & 0xFF);
        targets[1] = (byte) (i >> 16 & 0xFF);
        targets[0] = (byte) (i >> 24 & 0xFF);
        return targets;
    }

    public static int byte4ToInt(byte[] bytes) {
        int b0 = bytes[0] & 0xFF;
        int b1 = bytes[1] & 0xFF;
        int b2 = bytes[2] & 0xFF;
        int b3 = bytes[3] & 0xFF;
        return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
    }

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }

}
